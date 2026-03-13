package com.example.investplatform.application.service;

import com.example.investplatform.infrastructure.repository.RegistryOperationBasisRepository;
import com.example.investplatform.infrastructure.repository.RegistryOperationDocumentRepository;
import com.example.investplatform.infrastructure.repository.RegistryOperationRepository;
import com.example.investplatform.model.entity.account.PersonalAccount;
import com.example.investplatform.model.entity.registry.RegistryOperation;
import com.example.investplatform.model.entity.registry.RegistryOperationBasis;
import com.example.investplatform.model.entity.registry.RegistryOperationDocument;
import com.example.investplatform.model.entity.security.Security;
import com.example.investplatform.model.enums.OperationKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistryExportService {

    private static final String XSD_VERSION = "TRF_22_01";
    private static final String REFS_VERSION = "1";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final Charset WINDOWS_1251 = Charset.forName("windows-1251");

    private final RegistryOperationRepository operationRepository;
    private final RegistryOperationDocumentRepository documentRepository;
    private final RegistryOperationBasisRepository basisRepository;

    @Transactional(readOnly = true)
    public byte[] exportRegistrationBook(LocalDate dateFrom, LocalDate dateTo,
                                         String idDoc, String senderName, String senderId) {
        List<RegistryOperation> operations = operationRepository.findAllByDateStateBetween(dateFrom, dateTo);

        List<Long> operationIds = operations.stream()
                .map(RegistryOperation::getId)
                .toList();

        Map<Long, List<RegistryOperationDocument>> documentsByOpId = documentRepository
                .findByRegistryOperationIdIn(operationIds)
                .stream()
                .collect(Collectors.groupingBy(d -> d.getRegistryOperation().getId()));

        Map<Long, List<RegistryOperationBasis>> basisByOpId = basisRepository
                .findByRegistryOperationIdIn(operationIds)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getRegistryOperation().getId()));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter w = factory.createXMLStreamWriter(
                    new OutputStreamWriter(baos, WINDOWS_1251));

            w.writeStartDocument("windows-1251", "1.0");
            w.writeStartElement("REGISTRATION_BOOK_EXT");

            writeService(w, idDoc, senderName, senderId);

            for (RegistryOperation op : operations) {
                List<RegistryOperationDocument> docs = documentsByOpId.getOrDefault(op.getId(), List.of());
                List<RegistryOperationBasis> bases = basisByOpId.getOrDefault(op.getId(), List.of());
                writeRecord(w, op, docs, bases);
            }

            w.writeEndElement(); // REGISTRATION_BOOK_EXT
            w.writeEndDocument();
            w.flush();
            w.close();

            return baos.toByteArray();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Ошибка формирования XML реестра", e);
        }
    }

    private void writeService(XMLStreamWriter w, String idDoc,
                              String senderName, String senderId) throws XMLStreamException {
        w.writeStartElement("service");

        writeTextElement(w, "version", XSD_VERSION);
        writeTextElement(w, "version_refs", REFS_VERSION);

        // prvs_ref — ссылка на предыдущий документ (минимально обязательная)
        w.writeStartElement("prvs_ref");
        writeTextElement(w, "ref_name", XSD_VERSION);
        w.writeEndElement();

        writeTextElement(w, "id_doc", idDoc);

        // sender
        w.writeStartElement("sender");
        w.writeStartElement("id");
        writeTextElement(w, "id_value", senderId);
        w.writeEndElement(); // id
        writeTextElement(w, "name", senderName);
        w.writeEndElement(); // sender

        w.writeEndElement(); // service
    }

    private void writeRecord(XMLStreamWriter w, RegistryOperation op,
                             List<RegistryOperationDocument> docs,
                             List<RegistryOperationBasis> bases) throws XMLStreamException {
        w.writeStartElement("record");

        writeTextElement(w, "trn_id", String.valueOf(op.getId()));
        writeTextElement(w, "transaction_type", op.getOperationType().getCode());
        writeTextElement(w, "operation_name", op.getOperationName());

        // processing_datetime
        w.writeStartElement("processing_datetime");
        writeTextElement(w, "datetime", op.getProcessingDatetime().format(DATETIME_FMT));
        w.writeEndElement();

        writeTextElement(w, "processing_reference", op.getProcessingReference());

        if (op.getDateState() != null) {
            writeTextElement(w, "date_state", op.getDateState().format(DATE_FMT));
        }

        // foundation_document
        writeFoundationDocument(w, docs, bases);

        // operation choice based on OperationKind
        writeOperationChoice(w, op);

        w.writeEndElement(); // record
    }

    private void writeFoundationDocument(XMLStreamWriter w,
                                         List<RegistryOperationDocument> docs,
                                         List<RegistryOperationBasis> bases) throws XMLStreamException {
        w.writeStartElement("foundation_document");

        if (!docs.isEmpty()) {
            RegistryOperationDocument doc = docs.getFirst();
            writeTextElement(w, "in_doc_num", doc.getInDocNum());

            w.writeStartElement("in_reg_date");
            writeTextElement(w, "datetime", doc.getInRegDate().format(DATETIME_FMT));
            w.writeEndElement();

            if (doc.getOutDocNum() != null) {
                writeTextElement(w, "out_doc_num", doc.getOutDocNum());
            }
            if (doc.getOutDocDate() != null) {
                w.writeStartElement("out_doc_date");
                writeTextElement(w, "datetime", doc.getOutDocDate().format(DATETIME_FMT));
                w.writeEndElement();
            }
        } else {
            writeTextElement(w, "in_doc_num", "N/A");
            w.writeStartElement("in_reg_date");
            writeTextElement(w, "datetime", LocalDateTime.now().format(DATETIME_FMT));
            w.writeEndElement();
        }

        for (RegistryOperationBasis basis : bases) {
            w.writeStartElement("based_info");

            w.writeStartElement("contract_type");
            writeTextElement(w, "contract_code", basis.getContractType().getCode());
            if (basis.getContractNarrative() != null) {
                writeTextElement(w, "narrative", basis.getContractNarrative());
            }
            w.writeEndElement(); // contract_type

            writeTextElement(w, "doc_num", basis.getDocNum());
            writeTextElement(w, "doc_date", basis.getDocDate().format(DATE_FMT));

            w.writeEndElement(); // based_info
        }

        w.writeEndElement(); // foundation_document
    }

    private void writeOperationChoice(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        OperationKind kind = op.getOperationKind();
        if (kind == null) {
            kind = OperationKind.TRANSACTION;
        }

        switch (kind) {
            case TRANSACTION -> writeTransaction(w, op);
            case OPERATION_FORM -> writeOperationForm(w, op);
            case EMISSION -> writeOperationEmission(w, op);
            case BLOCKING -> writeBlockage(w, op);
            case NONPAYMENT -> writeNonpayment(w, op);
            case PLEDGE -> writePledge(w, op);
            case ASSIGNMENT_PLEDGE -> writeAssignmentPledge(w, op);
            case ESCROW -> writeEscrow(w, op);
            case OTHER -> writeOther(w, op);
        }
    }

    private void writeTransaction(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        w.writeStartElement("transaction");

        writeTextElement(w, "transaction_type", op.getOperationType().getCode());

        if (op.getAccountTransfer() != null) {
            writeAccountHolder(w, "account_transfer", op.getAccountTransfer());
        }
        if (op.getAccountReceive() != null) {
            writeAccountHolder(w, "account_receive", op.getAccountReceive());
        }

        writeSecurityInfo(w, op.getSecurity());
        writeQuantity(w, op.getQuantity());

        if (op.getSettlementAmount() != null) {
            writeSettlementAmount(w, op.getSettlementCurrency(), op.getSettlementAmount());
        }

        w.writeEndElement(); // transaction
    }

    private void writeOperationForm(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        w.writeStartElement("operation_form");

        writeTextElement(w, "transaction_type", op.getOperationType().getCode());

        if (op.getAccountTransfer() != null) {
            writeAccountHolder(w, "account", op.getAccountTransfer());
        }

        writeSecurityInfo(w, op.getSecurity());
        writeQuantity(w, op.getQuantity());

        if (op.getSettlementAmount() != null) {
            writeSettlementAmount(w, op.getSettlementCurrency(), op.getSettlementAmount());
        }

        w.writeEndElement();
    }

    private void writeOperationEmission(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        w.writeStartElement("operation_emission");

        writeTextElement(w, "transaction_type", op.getOperationType().getCode());

        if (op.getAccountTransfer() != null) {
            writeAccountHolder(w, "account_transfer", op.getAccountTransfer());
        }
        if (op.getAccountReceive() != null) {
            writeAccountHolder(w, "account_receive", op.getAccountReceive());
        }

        writeSecurityInfo(w, op.getSecurity());
        writeQuantity(w, op.getQuantity());

        w.writeEndElement();
    }

    private void writeBlockage(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        w.writeStartElement("blockage");

        writeTextElement(w, "transaction_type", op.getOperationType().getCode());

        if (op.getAccountTransfer() != null) {
            writeAccountHolder(w, "account", op.getAccountTransfer());
        }

        writeSecurityInfo(w, op.getSecurity());
        writeQuantity(w, op.getQuantity());

        if (op.getContent() != null) {
            writeTextElement(w, "comment", op.getContent());
        }

        w.writeEndElement();
    }

    private void writeNonpayment(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        w.writeStartElement("nonpayment");

        writeTextElement(w, "transaction_type", op.getOperationType().getCode());

        if (op.getAccountTransfer() != null) {
            writeAccountHolder(w, "account", op.getAccountTransfer());
        }

        writeSecurityInfo(w, op.getSecurity());

        if (op.getContent() != null) {
            writeTextElement(w, "comment", op.getContent());
        }

        w.writeEndElement();
    }

    private void writePledge(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        w.writeStartElement("pledge");

        writeTextElement(w, "transaction_type", op.getOperationType().getCode());

        if (op.getAccountTransfer() != null) {
            writeAccountHolder(w, "account_pledgor", op.getAccountTransfer());
        }
        if (op.getAccountReceive() != null) {
            writeAccountHolder(w, "account_pledgee", op.getAccountReceive());
        }

        writeSecurityInfo(w, op.getSecurity());
        writeQuantity(w, op.getQuantity());

        w.writeEndElement();
    }

    private void writeAssignmentPledge(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        w.writeStartElement("assigment_pledge");

        writeTextElement(w, "transaction_type", op.getOperationType().getCode());

        if (op.getAccountTransfer() != null) {
            writeAccountHolder(w, "account_pledgee_old", op.getAccountTransfer());
        }
        if (op.getAccountReceive() != null) {
            writeAccountHolder(w, "account_pledgee_new", op.getAccountReceive());
        }

        writeSecurityInfo(w, op.getSecurity());
        writeQuantity(w, op.getQuantity());

        w.writeEndElement();
    }

    private void writeEscrow(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        w.writeStartElement("escrow");

        writeTextElement(w, "transaction_type", op.getOperationType().getCode());

        if (op.getAccountTransfer() != null) {
            writeAccountHolder(w, "account", op.getAccountTransfer());
        }

        writeSecurityInfo(w, op.getSecurity());
        writeQuantity(w, op.getQuantity());

        w.writeEndElement();
    }

    private void writeOther(XMLStreamWriter w, RegistryOperation op) throws XMLStreamException {
        w.writeStartElement("other");

        writeTextElement(w, "transaction_type", op.getOperationType().getCode());

        if (op.getAccountTransfer() != null) {
            writeAccountHolder(w, "account", op.getAccountTransfer());
        }

        writeSecurityInfo(w, op.getSecurity());
        writeQuantity(w, op.getQuantity());

        if (op.getContent() != null) {
            writeTextElement(w, "comment", op.getContent());
        }

        w.writeEndElement();
    }

    private void writeAccountHolder(XMLStreamWriter w, String elementName,
                                    PersonalAccount account) throws XMLStreamException {
        w.writeStartElement(elementName);

        w.writeStartElement("account_dtls");
        writeTextElement(w, "account_id", account.getAccountNumber());
        writeTextElement(w, "account_type", account.getAccountType().getCode());
        w.writeEndElement(); // account_dtls

        w.writeEndElement();
    }

    private void writeSecurityInfo(XMLStreamWriter w, Security security) throws XMLStreamException {
        if (security == null) return;

        w.writeStartElement("security");
        writeTextElement(w, "securities_code", security.getSecuritiesCode());
        writeTextElement(w, "security_classification",
                security.getSecurityClassification().getCode());
        writeTextElement(w, "security_category",
                security.getSecurityCategory().getCode());
        if (security.getSecurityType() != null) {
            writeTextElement(w, "security_type", security.getSecurityType());
        }
        if (security.getNominalValue() != null) {
            w.writeStartElement("nominal_value");
            writeTextElement(w, "ccy_code", security.getNominalCurrency());
            writeTextElement(w, "amount", security.getNominalValue().toPlainString());
            w.writeEndElement();
        }
        writeTextElement(w, "state_reg_num", security.getStateRegNum());
        w.writeEndElement(); // security
    }

    private void writeQuantity(XMLStreamWriter w, java.math.BigDecimal quantity) throws XMLStreamException {
        if (quantity == null) return;

        w.writeStartElement("quantity");
        writeTextElement(w, "units", quantity.toPlainString());
        w.writeEndElement();
    }

    private void writeSettlementAmount(XMLStreamWriter w, String currency,
                                       java.math.BigDecimal amount) throws XMLStreamException {
        w.writeStartElement("settlement_amount");
        writeTextElement(w, "ccy_code", currency != null ? currency : "RUB");
        writeTextElement(w, "amount", amount.toPlainString());
        w.writeEndElement();
    }

    private void writeTextElement(XMLStreamWriter w, String name, String value) throws XMLStreamException {
        w.writeStartElement(name);
        w.writeCharacters(value != null ? value : "");
        w.writeEndElement();
    }
}
