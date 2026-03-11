package com.example.investplatform.application.service;

import com.example.investplatform.application.dto.emitent.CreateEmitentLegalEntityDto;
import com.example.investplatform.application.dto.emitent.CreateEmitentPrivateEntrepreneurDto;
import com.example.investplatform.application.dto.emitent.EmitentResponseDto;
import com.example.investplatform.application.exception.RoleNotFoundException;
import com.example.investplatform.application.exception.UsernameAlreadyTakenException;
import com.example.investplatform.infrastructure.repository.*;
import com.example.investplatform.infrastructure.security.RoleEnum;
import com.example.investplatform.model.entity.account.AccountType;
import com.example.investplatform.model.entity.account.PersonalAccount;
import com.example.investplatform.model.entity.emitent.*;
import com.example.investplatform.model.entity.user.Role;
import com.example.investplatform.model.entity.user.User;
import com.example.investplatform.model.enums.EmitentType;
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
public class EmitentService {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String OWNER_ACCOUNT_CODE = "01";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final PersonalAccountRepository personalAccountRepository;
    private final EmitentRepository emitentRepository;
    private final EmitentLegalEntityRepository legalEntityRepository;
    private final EmitentPrivateEntrepreneurRepository privateEntrepreneurRepository;
    private final EmitentDocumentRepository emitentDocumentRepository;
    private final EmitentDocumentTypeRepository emitentDocumentTypeRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmitentResponseDto createPrivateEntrepreneur(CreateEmitentPrivateEntrepreneurDto dto,
                                                        Map<String, MultipartFile> documents) {
        User user = createUser(dto.email(), dto.password());
        PersonalAccount account = createPersonalAccount();
        Emitent emitent = createEmitent(user, account, EmitentType.PRIVATE_ENTREPRENEUR);

        EmitentPrivateEntrepreneur pe = EmitentPrivateEntrepreneur.builder()
                .emitent(emitent)
                .lastName(dto.lastName())
                .firstName(dto.firstName())
                .patronymic(dto.patronymic())
                .birthDate(dto.birthDate())
                .birthPlace(dto.birthPlace())
                .ogrnip(dto.ogrnip())
                .inn(dto.inn())
                .registrationAddress(dto.registrationAddress())
                .snils(dto.snils())
                .materialFacts(dto.materialFacts())
                .investedCurrentYear(BigDecimal.ZERO)
                .build();
        privateEntrepreneurRepository.save(pe);

        saveDocuments(emitent, documents);

        return toResponse(emitent, user, account);
    }

    @Transactional
    public EmitentResponseDto createLegalEntity(CreateEmitentLegalEntityDto dto,
                                                Map<String, MultipartFile> documents) {
        User user = createUser(dto.email(), dto.password());
        PersonalAccount account = createPersonalAccount();
        Emitent emitent = createEmitent(user, account, EmitentType.LEGAL_ENTITY);

        EmitentLegalEntity le = EmitentLegalEntity.builder()
                .emitent(emitent)
                .fullName(dto.fullName())
                .shortName(dto.shortName())
                .ogrn(dto.ogrn())
                .inn(dto.inn())
                .kpp(dto.kpp())
                .legalAddress(dto.legalAddress())
                .postalAddress(dto.postalAddress())
                .okpo(dto.okpo())
                .okato(dto.okato())
                .organisationForm(dto.organisationForm())
                .materialFacts(dto.materialFacts())
                .investedCurrentYear(BigDecimal.ZERO)
                .build();
        legalEntityRepository.save(le);

        saveDocuments(emitent, documents);

        return toResponse(emitent, user, account);
    }

    private void saveDocuments(Emitent emitent, Map<String, MultipartFile> documents) {
        documents.forEach((typeCode, file) -> {
            EmitentDocumentType docType = emitentDocumentTypeRepository.findByCode(typeCode)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Неизвестный тип документа эмитента: " + typeCode));

            String objectKey = "emitents/%d/%s_%s".formatted(
                    emitent.getId(), typeCode, file.getOriginalFilename());
            fileStorageService.upload(file, objectKey);

            EmitentDocument doc = EmitentDocument.builder()
                    .emitent(emitent)
                    .documentType(docType)
                    .reportYear((short) LocalDate.now().getYear())
                    .fileName(file.getOriginalFilename())
                    .filePath(objectKey)
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .build();
            emitentDocumentRepository.save(doc);
        });
    }

    private User createUser(String email, String password) {
        if (userRepository.findByEmailWithRoles(email).isPresent()) {
            throw new UsernameAlreadyTakenException(
                    "Пользователь с email '%s' уже существует".formatted(email));
        }

        String roleName = ROLE_PREFIX + RoleEnum.EMITENT.name();
        Role emitentRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Роль '%s' не найдена".formatted(roleName)));

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .isEnabled(true)
                .isAccountNonLocked(true)
                .is2faEnabled(false)
                .roles(Set.of(emitentRole))
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

    private Emitent createEmitent(User user, PersonalAccount account, EmitentType type) {
        Emitent emitent = Emitent.builder()
                .user(user)
                .emitentType(type)
                .personalAccount(account)
                .build();
        return emitentRepository.save(emitent);
    }

    private String generateAccountNumber() {
        return "EMT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private EmitentResponseDto toResponse(Emitent emitent, User user, PersonalAccount account) {
        return new EmitentResponseDto(
                emitent.getId(),
                user.getEmail(),
                emitent.getEmitentType(),
                account.getAccountNumber()
        );
    }
}
