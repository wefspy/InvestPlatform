package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.investor.CreateInvestorIndividualDto;
import com.example.investplatform.application.dto.investor.CreateInvestorLegalEntityDto;
import com.example.investplatform.application.dto.investor.CreateInvestorPrivateEntrepreneurDto;
import com.example.investplatform.application.dto.investor.InvestorResponseDto;
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
import java.util.Map;
import java.util.Set;
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
        if (userRepository.findByEmailWithRoles(email).isPresent()) {
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
                .roles(Set.of(investorRole))
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
