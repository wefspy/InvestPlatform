package com.example.investplatform.application.service;

import com.example.investplatform.infrastructure.repository.*;
import com.example.investplatform.infrastructure.security.RoleEnum;
import com.example.investplatform.model.entity.emitent.Emitent;
import com.example.investplatform.model.entity.investor.Investor;
import com.example.investplatform.model.enums.EmitentType;
import com.example.investplatform.model.enums.InvestorType;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserDisplayNameResolver {

    private final OperatorRepository operatorRepository;
    private final InvestorRepository investorRepository;
    private final InvestorIndividualRepository investorIndividualRepository;
    private final InvestorPrivateEntrepreneurRepository investorPrivateEntrepreneurRepository;
    private final InvestorLegalEntityRepository investorLegalEntityRepository;
    private final EmitentRepository emitentRepository;
    private final EmitentPrivateEntrepreneurRepository emitentPrivateEntrepreneurRepository;
    private final EmitentLegalEntityRepository emitentLegalEntityRepository;

    public UserDisplayNameResolver(OperatorRepository operatorRepository,
                                   InvestorRepository investorRepository,
                                   InvestorIndividualRepository investorIndividualRepository,
                                   InvestorPrivateEntrepreneurRepository investorPrivateEntrepreneurRepository,
                                   InvestorLegalEntityRepository investorLegalEntityRepository,
                                   EmitentRepository emitentRepository,
                                   EmitentPrivateEntrepreneurRepository emitentPrivateEntrepreneurRepository,
                                   EmitentLegalEntityRepository emitentLegalEntityRepository) {
        this.operatorRepository = operatorRepository;
        this.investorRepository = investorRepository;
        this.investorIndividualRepository = investorIndividualRepository;
        this.investorPrivateEntrepreneurRepository = investorPrivateEntrepreneurRepository;
        this.investorLegalEntityRepository = investorLegalEntityRepository;
        this.emitentRepository = emitentRepository;
        this.emitentPrivateEntrepreneurRepository = emitentPrivateEntrepreneurRepository;
        this.emitentLegalEntityRepository = emitentLegalEntityRepository;
    }

    public String resolve(Long userId, Set<String> roles) {
        if (roles.contains(RoleEnum.OPERATOR.name())) {
            return resolveOperator(userId);
        }
        if (roles.contains(RoleEnum.INVESTOR.name())) {
            return resolveInvestor(userId);
        }
        if (roles.contains(RoleEnum.EMITENT.name())) {
            return resolveEmitent(userId);
        }
        return null;
    }

    private String resolveOperator(Long userId) {
        return operatorRepository.findByUserId(userId)
                .map(op -> joinNonBlank(op.getLastName(), op.getFirstName(), op.getPatronymic()))
                .orElse(null);
    }

    private String resolveInvestor(Long userId) {
        return investorRepository.findByUserId(userId)
                .map(this::resolveInvestorByType)
                .orElse(null);
    }

    private String resolveInvestorByType(Investor investor) {
        InvestorType type = investor.getInvestorType();
        Long investorId = investor.getId();

        return switch (type) {
            case INDIVIDUAL -> investorIndividualRepository.findByInvestorId(investorId)
                    .map(ind -> joinNonBlank(ind.getLastName(), ind.getFirstName(), ind.getPatronymic()))
                    .orElse(null);
            case PRIVATE_ENTREPRENEUR -> investorPrivateEntrepreneurRepository.findByInvestorId(investorId)
                    .map(pe -> joinNonBlank(pe.getLastName(), pe.getFirstName(), pe.getPatronymic()))
                    .orElse(null);
            case LEGAL_ENTITY -> investorLegalEntityRepository.findByInvestorId(investorId)
                    .map(le -> le.getShortName() != null ? le.getShortName() : le.getFullName())
                    .orElse(null);
        };
    }

    private String resolveEmitent(Long userId) {
        return emitentRepository.findByUserId(userId)
                .map(this::resolveEmitentByType)
                .orElse(null);
    }

    private String resolveEmitentByType(Emitent emitent) {
        EmitentType type = emitent.getEmitentType();
        Long emitentId = emitent.getId();

        return switch (type) {
            case PRIVATE_ENTREPRENEUR -> emitentPrivateEntrepreneurRepository.findByEmitentId(emitentId)
                    .map(pe -> joinNonBlank(pe.getLastName(), pe.getFirstName(), pe.getPatronymic()))
                    .orElse(null);
            case LEGAL_ENTITY -> emitentLegalEntityRepository.findByEmitentId(emitentId)
                    .map(le -> le.getShortName() != null ? le.getShortName() : le.getFullName())
                    .orElse(null);
        };
    }

    private String joinNonBlank(String... parts) {
        return Stream.of(parts)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
    }
}
