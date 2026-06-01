package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.investor.CreateInvestorIndividualDto;
import com.example.investplatform.application.dto.investor.CreateInvestorLegalEntityDto;
import com.example.investplatform.application.dto.investor.CreateInvestorPrivateEntrepreneurDto;
import com.example.investplatform.application.dto.investor.InvestorDetailDto;
import com.example.investplatform.application.dto.investor.InvestorDocumentResponseDto;
import com.example.investplatform.application.dto.investor.InvestorIndividualDataDto;
import com.example.investplatform.application.dto.investor.InvestorLegalEntityDataDto;
import com.example.investplatform.application.dto.investor.InvestorPrivateEntrepreneurDataDto;
import com.example.investplatform.application.dto.investor.InvestorResponseDto;
import com.example.investplatform.application.dto.investor.UpdateInvestorIndividualDto;
import com.example.investplatform.application.dto.investor.UpdateInvestorLegalEntityDto;
import com.example.investplatform.application.dto.investor.UpdateInvestorPrivateEntrepreneurDto;
import com.example.investplatform.application.exception.RoleNotFoundException;
import com.example.investplatform.application.exception.UsernameAlreadyTakenException;
import com.example.investplatform.infrastructure.repository.*;
import com.example.investplatform.infrastructure.security.RoleEnum;
import com.example.investplatform.model.entity.account.AccountType;
import com.example.investplatform.model.entity.account.PersonalAccount;
import com.example.investplatform.model.entity.investor.*;
import com.example.investplatform.model.entity.user.Role;
import com.example.investplatform.model.entity.user.User;
import com.example.investplatform.model.enums.InvestorType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestorService {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String OWNER_ACCOUNT_CODE = "01";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final PersonalAccountRepository personalAccountRepository;
    private final InvestorRepository investorRepository;
    private final InvestorIndividualRepository individualRepository;
    private final InvestorPrivateEntrepreneurRepository privateEntrepreneurRepository;
    private final InvestorLegalEntityRepository legalEntityRepository;
    private final InvestorDocumentRepository investorDocumentRepository;
    private final InvestorDocumentTypeRepository investorDocumentTypeRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public InvestorResponseDto createIndividual(CreateInvestorIndividualDto dto,
                                                Map<String, MultipartFile> documents) {
        User user = createUser(dto.email(), dto.password());
        PersonalAccount account = createPersonalAccount();
        Investor investor = createInvestor(user, account, InvestorType.INDIVIDUAL);

        InvestorIndividual individual = InvestorIndividual.builder()
                .investor(investor)
                .lastName(dto.lastName())
                .firstName(dto.firstName())
                .patronymic(dto.patronymic())
                .gender(dto.gender())
                .citizenship(dto.citizenship())
                .birthDate(dto.birthDate())
                .birthPlace(dto.birthPlace())
                .idDocType(dto.idDocType())
                .idDocSeries(dto.idDocSeries())
                .idDocNumber(dto.idDocNumber())
                .idDocIssuedDate(dto.idDocIssuedDate())
                .idDocIssuedBy(dto.idDocIssuedBy())
                .idDocDepartmentCode(dto.idDocDepartmentCode())
                .registrationAddress(dto.registrationAddress())
                .residentialAddress(dto.residentialAddress())
                .inn(dto.inn())
                .snils(dto.snils())
                .email(dto.contactEmail())
                .phone(dto.phone())
                .investedOtherPlatforms(
                        dto.investedOtherPlatforms() != null ? dto.investedOtherPlatforms() : BigDecimal.ZERO)
                .build();
        individualRepository.save(individual);

        saveDocuments(investor, documents);

        return toResponse(investor, user, account);
    }

    @Transactional
    public InvestorResponseDto createPrivateEntrepreneur(CreateInvestorPrivateEntrepreneurDto dto,
                                                         Map<String, MultipartFile> documents) {
        User user = createUser(dto.email(), dto.password());
        PersonalAccount account = createPersonalAccount();
        Investor investor = createInvestor(user, account, InvestorType.PRIVATE_ENTREPRENEUR);

        InvestorPrivateEntrepreneur pe = InvestorPrivateEntrepreneur.builder()
                .investor(investor)
                .lastName(dto.lastName())
                .firstName(dto.firstName())
                .patronymic(dto.patronymic())
                .gender(dto.gender())
                .citizenship(dto.citizenship())
                .birthDate(dto.birthDate())
                .birthPlace(dto.birthPlace())
                .idDocType(dto.idDocType())
                .idDocSeries(dto.idDocSeries())
                .idDocNumber(dto.idDocNumber())
                .idDocIssuedDate(dto.idDocIssuedDate())
                .idDocIssuedBy(dto.idDocIssuedBy())
                .idDocDepartmentCode(dto.idDocDepartmentCode())
                .registrationAddress(dto.registrationAddress())
                .residentialAddress(dto.residentialAddress())
                .inn(dto.inn())
                .snils(dto.snils())
                .ogrnip(dto.ogrnip())
                .foreignRegistrationInfo(dto.foreignRegistrationInfo())
                .email(dto.contactEmail())
                .phone(dto.phone())
                .build();
        privateEntrepreneurRepository.save(pe);

        saveDocuments(investor, documents);

        return toResponse(investor, user, account);
    }

    @Transactional
    public InvestorResponseDto createLegalEntity(CreateInvestorLegalEntityDto dto,
                                                 Map<String, MultipartFile> documents) {
        User user = createUser(dto.email(), dto.password());
        PersonalAccount account = createPersonalAccount();
        Investor investor = createInvestor(user, account, InvestorType.LEGAL_ENTITY);

        InvestorLegalEntity le = InvestorLegalEntity.builder()
                .investor(investor)
                .fullName(dto.fullName())
                .shortName(dto.shortName())
                .ogrn(dto.ogrn())
                .inn(dto.inn())
                .foreignRegistrationInfo(dto.foreignRegistrationInfo())
                .tin(dto.tin())
                .legalAddress(dto.legalAddress())
                .postalAddress(dto.postalAddress())
                .email(dto.contactEmail())
                .phone(dto.phone())
                .build();
        legalEntityRepository.save(le);

        saveDocuments(investor, documents);

        return toResponse(investor, user, account);
    }

    @Transactional(readOnly = true)
    public InvestorDetailDto getById(Long investorId) {
        Investor investor = findInvestorOrThrow(investorId);

        InvestorIndividualDataDto individualData = null;
        InvestorPrivateEntrepreneurDataDto peData = null;
        InvestorLegalEntityDataDto leData = null;

        switch (investor.getInvestorType()) {
            case INDIVIDUAL -> individualData = individualRepository.findByInvestorId(investorId)
                    .map(this::toIndividualData)
                    .orElse(null);
            case PRIVATE_ENTREPRENEUR -> peData = privateEntrepreneurRepository.findByInvestorId(investorId)
                    .map(this::toPeData)
                    .orElse(null);
            case LEGAL_ENTITY -> leData = legalEntityRepository.findByInvestorId(investorId)
                    .map(this::toLeData)
                    .orElse(null);
        }

        return new InvestorDetailDto(
                investor.getId(),
                investor.getUser().getEmail(),
                investor.getInvestorType(),
                investor.getPersonalAccount().getAccountNumber(),
                investor.getIsQualified(),
                investor.getRiskDeclarationAccepted(),
                individualData,
                peData,
                leData);
    }

    @Transactional
    public InvestorResponseDto updateIndividual(Long investorId, UpdateInvestorIndividualDto dto) {
        Investor investor = findInvestorOrThrow(investorId);

        InvestorIndividual individual = individualRepository.findByInvestorId(investorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Инвестор с ID %d не является физическим лицом".formatted(investorId)));

        individual.setLastName(dto.lastName());
        individual.setFirstName(dto.firstName());
        individual.setPatronymic(dto.patronymic());
        individual.setGender(dto.gender());
        individual.setCitizenship(dto.citizenship());
        individual.setBirthDate(dto.birthDate());
        individual.setBirthPlace(dto.birthPlace());
        individual.setIdDocType(dto.idDocType());
        individual.setIdDocSeries(dto.idDocSeries());
        individual.setIdDocNumber(dto.idDocNumber());
        individual.setIdDocIssuedDate(dto.idDocIssuedDate());
        individual.setIdDocIssuedBy(dto.idDocIssuedBy());
        individual.setIdDocDepartmentCode(dto.idDocDepartmentCode());
        individual.setRegistrationAddress(dto.registrationAddress());
        individual.setResidentialAddress(dto.residentialAddress());
        individual.setInn(dto.inn());
        individual.setSnils(dto.snils());
        individual.setEmail(dto.contactEmail());
        individual.setPhone(dto.phone());
        if (dto.investedOtherPlatforms() != null) {
            individual.setInvestedOtherPlatforms(dto.investedOtherPlatforms());
        }
        individualRepository.save(individual);

        return toResponse(investor, investor.getUser(), investor.getPersonalAccount());
    }

    @Transactional
    public InvestorResponseDto updatePrivateEntrepreneur(Long investorId,
                                                         UpdateInvestorPrivateEntrepreneurDto dto) {
        Investor investor = findInvestorOrThrow(investorId);

        InvestorPrivateEntrepreneur pe = privateEntrepreneurRepository.findByInvestorId(investorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Инвестор с ID %d не является индивидуальным предпринимателем".formatted(investorId)));

        pe.setLastName(dto.lastName());
        pe.setFirstName(dto.firstName());
        pe.setPatronymic(dto.patronymic());
        pe.setGender(dto.gender());
        pe.setCitizenship(dto.citizenship());
        pe.setBirthDate(dto.birthDate());
        pe.setBirthPlace(dto.birthPlace());
        pe.setIdDocType(dto.idDocType());
        pe.setIdDocSeries(dto.idDocSeries());
        pe.setIdDocNumber(dto.idDocNumber());
        pe.setIdDocIssuedDate(dto.idDocIssuedDate());
        pe.setIdDocIssuedBy(dto.idDocIssuedBy());
        pe.setIdDocDepartmentCode(dto.idDocDepartmentCode());
        pe.setRegistrationAddress(dto.registrationAddress());
        pe.setResidentialAddress(dto.residentialAddress());
        pe.setInn(dto.inn());
        pe.setSnils(dto.snils());
        pe.setOgrnip(dto.ogrnip());
        pe.setForeignRegistrationInfo(dto.foreignRegistrationInfo());
        pe.setEmail(dto.contactEmail());
        pe.setPhone(dto.phone());
        privateEntrepreneurRepository.save(pe);

        return toResponse(investor, investor.getUser(), investor.getPersonalAccount());
    }

    @Transactional
    public InvestorResponseDto updateLegalEntity(Long investorId, UpdateInvestorLegalEntityDto dto) {
        Investor investor = findInvestorOrThrow(investorId);

        InvestorLegalEntity le = legalEntityRepository.findByInvestorId(investorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Инвестор с ID %d не является юридическим лицом".formatted(investorId)));

        le.setFullName(dto.fullName());
        le.setShortName(dto.shortName());
        le.setOgrn(dto.ogrn());
        le.setInn(dto.inn());
        le.setForeignRegistrationInfo(dto.foreignRegistrationInfo());
        le.setTin(dto.tin());
        le.setLegalAddress(dto.legalAddress());
        le.setPostalAddress(dto.postalAddress());
        le.setEmail(dto.contactEmail());
        le.setPhone(dto.phone());
        legalEntityRepository.save(le);

        return toResponse(investor, investor.getUser(), investor.getPersonalAccount());
    }

    @Transactional(readOnly = true)
    public List<InvestorDocumentResponseDto> getDocuments(Long investorId) {
        findInvestorOrThrow(investorId);
        return investorDocumentRepository.findByInvestorId(investorId).stream()
                .map(this::toDocumentResponse)
                .toList();
    }

    @Transactional
    public InvestorDocumentResponseDto addDocument(Long investorId, String typeCode, MultipartFile file) {
        Investor investor = findInvestorOrThrow(investorId);

        InvestorDocumentType docType = investorDocumentTypeRepository.findByCode(typeCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Неизвестный тип документа инвестора: " + typeCode));

        String objectKey = "investors/%d/%s_%s".formatted(
                investorId, typeCode, file.getOriginalFilename());
        fileStorageService.upload(file, objectKey);

        InvestorDocument doc = InvestorDocument.builder()
                .investor(investor)
                .documentType(docType)
                .reportYear((short) LocalDate.now().getYear())
                .fileName(file.getOriginalFilename())
                .filePath(objectKey)
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .build();
        doc = investorDocumentRepository.save(doc);

        return toDocumentResponse(doc);
    }

    @Transactional
    public void deleteDocument(Long investorId, Long documentId) {
        findInvestorOrThrow(investorId);

        InvestorDocument document = investorDocumentRepository.findByIdAndInvestorId(documentId, investorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Документ с ID %d не найден у инвестора с ID %d".formatted(documentId, investorId)));

        fileStorageService.delete(document.getFilePath());
        investorDocumentRepository.delete(document);
    }

    @Transactional
    public InvestorDocumentResponseDto replaceDocument(Long investorId, Long documentId,
                                                       MultipartFile file) {
        findInvestorOrThrow(investorId);

        InvestorDocument document = investorDocumentRepository.findByIdAndInvestorId(documentId, investorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Документ с ID %d не найден у инвестора с ID %d".formatted(documentId, investorId)));

        fileStorageService.delete(document.getFilePath());

        String objectKey = "investors/%d/%s_%s".formatted(
                investorId, document.getDocumentType().getCode(), file.getOriginalFilename());
        fileStorageService.upload(file, objectKey);

        document.setFileName(file.getOriginalFilename());
        document.setFilePath(objectKey);
        document.setFileSize(file.getSize());
        document.setMimeType(file.getContentType());
        document = investorDocumentRepository.save(document);

        return toDocumentResponse(document);
    }

    private Investor findInvestorOrThrow(Long investorId) {
        return investorRepository.findById(investorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Инвестор с ID %d не найден".formatted(investorId)));
    }

    private InvestorIndividualDataDto toIndividualData(InvestorIndividual ind) {
        return new InvestorIndividualDataDto(
                ind.getLastName(),
                ind.getFirstName(),
                ind.getPatronymic(),
                ind.getGender(),
                ind.getCitizenship(),
                ind.getBirthDate(),
                ind.getBirthPlace(),
                ind.getIdDocType(),
                ind.getIdDocSeries(),
                ind.getIdDocNumber(),
                ind.getIdDocIssuedDate(),
                ind.getIdDocIssuedBy(),
                ind.getIdDocDepartmentCode(),
                ind.getRegistrationAddress(),
                ind.getResidentialAddress(),
                ind.getInn(),
                ind.getSnils(),
                ind.getEmail(),
                ind.getPhone(),
                ind.getInvestedOtherPlatforms());
    }

    private InvestorPrivateEntrepreneurDataDto toPeData(InvestorPrivateEntrepreneur pe) {
        return new InvestorPrivateEntrepreneurDataDto(
                pe.getLastName(),
                pe.getFirstName(),
                pe.getPatronymic(),
                pe.getGender(),
                pe.getCitizenship(),
                pe.getBirthDate(),
                pe.getBirthPlace(),
                pe.getIdDocType(),
                pe.getIdDocSeries(),
                pe.getIdDocNumber(),
                pe.getIdDocIssuedDate(),
                pe.getIdDocIssuedBy(),
                pe.getIdDocDepartmentCode(),
                pe.getRegistrationAddress(),
                pe.getResidentialAddress(),
                pe.getInn(),
                pe.getSnils(),
                pe.getOgrnip(),
                pe.getForeignRegistrationInfo(),
                pe.getEmail(),
                pe.getPhone());
    }

    private InvestorLegalEntityDataDto toLeData(InvestorLegalEntity le) {
        return new InvestorLegalEntityDataDto(
                le.getFullName(),
                le.getShortName(),
                le.getOgrn(),
                le.getInn(),
                le.getForeignRegistrationInfo(),
                le.getTin(),
                le.getLegalAddress(),
                le.getPostalAddress(),
                le.getEmail(),
                le.getPhone());
    }

    private InvestorDocumentResponseDto toDocumentResponse(InvestorDocument doc) {
        return new InvestorDocumentResponseDto(
                doc.getId(),
                doc.getDocumentType().getCode(),
                doc.getDocumentType().getName(),
                doc.getFileName(),
                doc.getFilePath(),
                doc.getFileSize(),
                doc.getMimeType(),
                doc.getUploadedAt());
    }

    private void saveDocuments(Investor investor, Map<String, MultipartFile> documents) {
        documents.forEach((typeCode, file) -> {
            InvestorDocumentType docType = investorDocumentTypeRepository.findByCode(typeCode)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Неизвестный тип документа инвестора: " + typeCode));

            String objectKey = "investors/%d/%s_%s".formatted(
                    investor.getId(), typeCode, file.getOriginalFilename());
            fileStorageService.upload(file, objectKey);

            InvestorDocument doc = InvestorDocument.builder()
                    .investor(investor)
                    .documentType(docType)
                    .reportYear((short) LocalDate.now().getYear())
                    .fileName(file.getOriginalFilename())
                    .filePath(objectKey)
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .build();
            investorDocumentRepository.save(doc);
        });
    }

    private User createUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UsernameAlreadyTakenException(
                    "Пользователь с email '%s' уже существует".formatted(email));
        }

        String roleName = ROLE_PREFIX + RoleEnum.INVESTOR.name();
        Role investorRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Роль '%s' не найдена".formatted(roleName)));

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .isEnabled(true)
                .isAccountNonLocked(true)
                .is2faEnabled(false)
                .role(investorRole)
                .build();
        return userRepository.save(user);
    }

    private PersonalAccount createPersonalAccount() {
        AccountType ownerType = accountTypeRepository.findByCode(OWNER_ACCOUNT_CODE)
                .orElseThrow(() -> new IllegalStateException(
                        "Тип счёта с кодом '%s' не найден. Проверьте миграции Liquibase."
                                .formatted(OWNER_ACCOUNT_CODE)));

        PersonalAccount account = PersonalAccount.builder()
                .accountNumber(generateAccountNumber())
                .accountType(ownerType)
                .balance(BigDecimal.ZERO)
                .holdAmount(BigDecimal.ZERO)
                .build();
        return personalAccountRepository.save(account);
    }

    private Investor createInvestor(User user, PersonalAccount account, InvestorType type) {
        Investor investor = Investor.builder()
                .user(user)
                .investorType(type)
                .personalAccount(account)
                .isQualified(false)
                .riskDeclarationAccepted(false)
                .build();
        return investorRepository.save(investor);
    }

    private String generateAccountNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private InvestorResponseDto toResponse(Investor investor, User user, PersonalAccount account) {
        return new InvestorResponseDto(
                investor.getId(),
                user.getEmail(),
                investor.getInvestorType(),
                account.getAccountNumber()
        );
    }
}
