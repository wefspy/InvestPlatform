--liquibase formatted sql

--changeset investplatform:001-create-roles
CREATE TABLE roles (
    id   SERIAL      PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_OPERATOR'),
    ('ROLE_EMITENT'),
    ('ROLE_INVESTOR');

--changeset investplatform:001-create-users
CREATE TABLE users (
    id         BIGSERIAL    PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id    INTEGER      NOT NULL REFERENCES roles(id),
    is_enabled BOOLEAN      NOT NULL DEFAULT FALSE,
    is_account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    is_2fa_enabled BOOLEAN  NOT NULL DEFAULT FALSE,
    two_fa_secret_hash VARCHAR(255),
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now(),
    version    BIGINT       NOT NULL DEFAULT 0
);

--changeset investplatform:001-create-operators
CREATE TABLE operators (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL UNIQUE REFERENCES users(id),
    last_name  VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255)
);

--changeset investplatform:001-create-account-types
CREATE TABLE account_types (
    id   SERIAL       PRIMARY KEY,
    code VARCHAR(10)  NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO account_types (code, name) VALUES
    ('01', 'Счёт владельца'),
    ('02', 'Счёт номинального держателя'),
    ('03', 'Счёт доверительного управляющего'),
    ('04', 'Счёт залогодержателя'),
    ('09', 'Счёт нотариуса'),
    ('12', 'Счёт номинального держателя центрального депозитария'),
    ('18', 'Счёт доверительного управляющего правами'),
    ('19', 'Депозитный счёт'),
    ('20', 'Счёт иностранного номинального держателя'),
    ('21', 'Счёт иностранного уполномоченного держателя'),
    ('22', 'Счёт депозитарных программ'),
    ('32', 'Эскроу-агент'),
    ('33', 'Казначейский счёт'),
    ('99', 'Счёт платформы');

--changeset investplatform:001-create-personal-accounts
CREATE TABLE personal_accounts (
    id              BIGSERIAL      PRIMARY KEY,
    account_number  VARCHAR(50)    NOT NULL UNIQUE,
    account_type_id INTEGER        NOT NULL REFERENCES account_types(id),
    balance         DECIMAL(18,2)  NOT NULL DEFAULT 0.00 CHECK (balance >= 0),
    hold_amount     DECIMAL(18,2)  NOT NULL DEFAULT 0.00 CHECK (hold_amount >= 0),
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP      NOT NULL DEFAULT now(),
    version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_personal_accounts_type ON personal_accounts (account_type_id);

INSERT INTO personal_accounts (account_number, account_type_id, balance, hold_amount)
VALUES ('PLATFORM-001', (SELECT id FROM account_types WHERE code = '99'), 0.00, 0.00);

--changeset investplatform:001-create-emitents
CREATE TABLE emitents (
    id                  BIGSERIAL    PRIMARY KEY,
    user_id             BIGINT       NOT NULL UNIQUE REFERENCES users(id),
    emitent_type        VARCHAR(30)  NOT NULL CHECK (emitent_type IN ('PRIVATE_ENTREPRENEUR', 'LEGAL_ENTITY')),
    personal_account_id BIGINT       NOT NULL UNIQUE REFERENCES personal_accounts(id)
);

CREATE INDEX idx_emitents_type ON emitents (emitent_type);

--changeset investplatform:001-create-emitent-legal-entities
CREATE TABLE emitent_legal_entities (
    id                    BIGSERIAL     PRIMARY KEY,
    emitent_id            BIGINT        NOT NULL UNIQUE REFERENCES emitents(id),
    full_name             VARCHAR(500)  NOT NULL,
    short_name            VARCHAR(255),
    ogrn                  VARCHAR(13)   NOT NULL UNIQUE,
    inn                   VARCHAR(10)   NOT NULL UNIQUE,
    kpp                   VARCHAR(9),
    legal_address         TEXT          NOT NULL,
    postal_address        TEXT,
    okpo                  VARCHAR(10),
    okato                 VARCHAR(11),
    organisation_form     VARCHAR(100),
    material_facts        TEXT,
    invested_current_year DECIMAL(18,2) NOT NULL DEFAULT 0.00 CHECK (invested_current_year >= 0),
    version              BIGINT        NOT NULL DEFAULT 0
);

--changeset investplatform:001-create-emitent-private-entrepreneurs
CREATE TABLE emitent_private_entrepreneurs (
    id                    BIGSERIAL     PRIMARY KEY,
    emitent_id            BIGINT        NOT NULL UNIQUE REFERENCES emitents(id),
    last_name             VARCHAR(255)  NOT NULL,
    first_name            VARCHAR(255)  NOT NULL,
    patronymic            VARCHAR(255),
    birth_date            DATE          NOT NULL,
    birth_place           VARCHAR(500)  NOT NULL,
    ogrnip                VARCHAR(15)   NOT NULL UNIQUE,
    inn                   VARCHAR(12)   NOT NULL UNIQUE,
    registration_address  TEXT          NOT NULL,
    snils                 VARCHAR(11)   NOT NULL UNIQUE,
    material_facts        TEXT,
    invested_current_year DECIMAL(18,2) NOT NULL DEFAULT 0.00 CHECK (invested_current_year >= 0),
    version              BIGINT        NOT NULL DEFAULT 0
);

--changeset investplatform:001-create-emitent-le-shareholders
CREATE TABLE emitent_le_shareholders (
    id                      BIGSERIAL     PRIMARY KEY,
    emitent_legal_entity_id BIGINT        NOT NULL REFERENCES emitent_legal_entities(id),
    person_type             VARCHAR(30)   NOT NULL CHECK (person_type IN ('INDIVIDUAL', 'LEGAL_ENTITY')),
    full_name               VARCHAR(500)  NOT NULL,
    country                 VARCHAR(100)  NOT NULL,
    inn                     VARCHAR(12)   NOT NULL,
    ogrn                    VARCHAR(13),
    foreign_registration_info TEXT,
    vote_share_percent      DECIMAL(5,2)  NOT NULL CHECK (vote_share_percent >= 10 AND vote_share_percent <= 100),
    ownership_basis         VARCHAR(500)  NOT NULL
);

CREATE INDEX idx_emitent_le_shareholders_entity ON emitent_le_shareholders (emitent_legal_entity_id);

--changeset investplatform:001-create-emitent-le-governing-bodies
CREATE TABLE emitent_le_governing_bodies (
    id                      BIGSERIAL    PRIMARY KEY,
    emitent_legal_entity_id BIGINT       NOT NULL REFERENCES emitent_legal_entities(id),
    body_name               VARCHAR(255) NOT NULL
);

CREATE INDEX idx_emitent_le_governing_bodies_entity ON emitent_le_governing_bodies (emitent_legal_entity_id);

--changeset investplatform:001-create-emitent-le-governing-members
CREATE TABLE emitent_le_governing_members (
    id                      BIGSERIAL    PRIMARY KEY,
    governing_body_id       BIGINT       NOT NULL REFERENCES emitent_le_governing_bodies(id),
    person_type             VARCHAR(20)  NOT NULL CHECK (person_type IN ('INDIVIDUAL', 'LEGAL_ENTITY')),
    full_name               VARCHAR(500) NOT NULL,
    country                 VARCHAR(100) NOT NULL,
    inn                     VARCHAR(12)  NOT NULL,
    ogrn                    VARCHAR(13),
    foreign_registration_info TEXT
);

CREATE INDEX idx_emitent_le_governing_members_body ON emitent_le_governing_members (governing_body_id);

--changeset investplatform:001-create-emitent-le-minority-info
CREATE TABLE emitent_le_minority_info (
    id                      BIGSERIAL   PRIMARY KEY,
    emitent_legal_entity_id BIGINT      NOT NULL UNIQUE REFERENCES emitent_legal_entities(id),
    total_shares_count      INTEGER     NOT NULL,
    total_share_percent     DECIMAL(5,2) NOT NULL
);

--changeset investplatform:001-create-okveds
CREATE TABLE okveds (
    id   SERIAL       PRIMARY KEY,
    code VARCHAR(10)  NOT NULL UNIQUE,
    name VARCHAR(500) NOT NULL UNIQUE
);

--changeset investplatform:001-create-emitent-okved
CREATE TABLE emitent_okved (
    emitent_id BIGINT  NOT NULL REFERENCES emitents(id),
    okved_id   INTEGER NOT NULL REFERENCES okveds(id),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (emitent_id, okved_id)
);

--changeset investplatform:001-create-emitent-document-types
CREATE TABLE emitent_document_types (
    id   SERIAL       PRIMARY KEY,
    code VARCHAR(50)  NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO emitent_document_types (code, name) VALUES
    ('financial_report',    'Годовая бухгалтерская (финансовая) отчётность'),
    ('audit_conclusion',    'Аудиторское заключение'),
    ('charter',             'Устав'),
    ('egrul_extract',       'Выписка из ЕГРЮЛ'),
    ('other',               'Иной документ');

--changeset investplatform:001-create-emitent-documents
CREATE TABLE emitent_documents (
    id               BIGSERIAL      PRIMARY KEY,
    emitent_id       BIGINT         NOT NULL REFERENCES emitents(id),
    document_type_id INTEGER        NOT NULL REFERENCES emitent_document_types(id),
    report_year      SMALLINT       NOT NULL,
    file_name        VARCHAR(500)   NOT NULL,
    file_path        VARCHAR(1024)  NOT NULL,
    file_size        BIGINT,
    mime_type        VARCHAR(100),
    uploaded_at      TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_emitent_documents_composite ON emitent_documents (emitent_id, document_type_id, report_year);
CREATE INDEX idx_emitent_documents_type ON emitent_documents (document_type_id);

--changeset investplatform:001-create-investors
CREATE TABLE investors (
    id                          BIGSERIAL    PRIMARY KEY,
    user_id                     BIGINT       NOT NULL UNIQUE REFERENCES users(id),
    investor_type               VARCHAR(30)  NOT NULL CHECK (investor_type IN ('INDIVIDUAL', 'PRIVATE_ENTREPRENEUR', 'LEGAL_ENTITY')),
    personal_account_id         BIGINT       NOT NULL UNIQUE REFERENCES personal_accounts(id),
    is_qualified                BOOLEAN      DEFAULT FALSE,
    qualified_at                TIMESTAMP,
    qualified_basis             VARCHAR(500),
    risk_declaration_accepted   BOOLEAN      DEFAULT FALSE,
    risk_accepted_at            TIMESTAMP,
    version                     BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_investors_type ON investors (investor_type);
CREATE INDEX idx_investors_qualified ON investors (is_qualified, qualified_at);

ALTER TABLE investors ADD CONSTRAINT chk_qualified_date
    CHECK (is_qualified = FALSE OR qualified_at IS NOT NULL);
ALTER TABLE investors ADD CONSTRAINT chk_risk_date
    CHECK (risk_declaration_accepted = FALSE OR risk_accepted_at IS NOT NULL);

--changeset investplatform:001-create-investor-individuals
CREATE TABLE investor_individuals (
    id                      BIGSERIAL     PRIMARY KEY,
    investor_id             BIGINT        NOT NULL UNIQUE REFERENCES investors(id),
    last_name               VARCHAR(255)  NOT NULL,
    first_name              VARCHAR(255)  NOT NULL,
    patronymic              VARCHAR(255),
    gender                  VARCHAR(15)   NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    citizenship             VARCHAR(100)  NOT NULL,
    birth_date              DATE          NOT NULL,
    birth_place             VARCHAR(500)  NOT NULL,
    id_doc_type             VARCHAR(50)   NOT NULL,
    id_doc_series           VARCHAR(20),
    id_doc_number           VARCHAR(50)   NOT NULL,
    id_doc_issued_date      DATE          NOT NULL,
    id_doc_issued_by        VARCHAR(500)  NOT NULL,
    id_doc_department_code  VARCHAR(10),
    registration_address    TEXT          NOT NULL,
    residential_address     TEXT          NOT NULL,
    inn                     VARCHAR(12),
    snils                   VARCHAR(14),
    email                   VARCHAR(255)  NOT NULL,
    phone                   VARCHAR(20)   NOT NULL,
    invested_other_platforms DECIMAL(18,2) NOT NULL DEFAULT 0.00 CHECK (invested_other_platforms >= 0)
);

--changeset investplatform:001-create-investor-private-entrepreneurs
CREATE TABLE investor_private_entrepreneurs (
    id                      BIGSERIAL     PRIMARY KEY,
    investor_id             BIGINT        NOT NULL UNIQUE REFERENCES investors(id),
    last_name               VARCHAR(255)  NOT NULL,
    first_name              VARCHAR(255)  NOT NULL,
    patronymic              VARCHAR(255),
    gender                  VARCHAR(15)   NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    citizenship             VARCHAR(100)  NOT NULL,
    birth_date              DATE          NOT NULL,
    birth_place             VARCHAR(500)  NOT NULL,
    id_doc_type             VARCHAR(50)   NOT NULL,
    id_doc_series           VARCHAR(20),
    id_doc_number           VARCHAR(50)   NOT NULL,
    id_doc_issued_date      DATE          NOT NULL,
    id_doc_issued_by        VARCHAR(500)  NOT NULL,
    id_doc_department_code  VARCHAR(10),
    registration_address    TEXT          NOT NULL,
    residential_address     TEXT          NOT NULL,
    inn                     VARCHAR(12),
    snils                   VARCHAR(14),
    ogrnip                  VARCHAR(15),
    foreign_registration_info TEXT,
    email                   VARCHAR(255)  NOT NULL,
    phone                   VARCHAR(20)   NOT NULL
);

--changeset investplatform:001-create-investor-legal-entities
CREATE TABLE investor_legal_entities (
    id                      BIGSERIAL     PRIMARY KEY,
    investor_id             BIGINT        NOT NULL UNIQUE REFERENCES investors(id),
    full_name               VARCHAR(500)  NOT NULL,
    short_name              VARCHAR(255),
    ogrn                    VARCHAR(13)   NOT NULL UNIQUE,
    inn                     VARCHAR(10)   NOT NULL UNIQUE,
    foreign_registration_info TEXT,
    tin                     VARCHAR(50),
    legal_address           TEXT          NOT NULL,
    postal_address          TEXT,
    email                   VARCHAR(255)  NOT NULL,
    phone                   VARCHAR(20)   NOT NULL
);

--changeset investplatform:001-create-investor-le-executives
CREATE TABLE investor_le_executives (
    id                      BIGSERIAL     PRIMARY KEY,
    investor_legal_entity_id BIGINT       NOT NULL REFERENCES investor_legal_entities(id),
    is_management_company   BOOLEAN       DEFAULT FALSE,
    last_name               VARCHAR(255)  NOT NULL,
    first_name              VARCHAR(255)  NOT NULL,
    patronymic              VARCHAR(255),
    gender                  VARCHAR(15)   NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    citizenship             VARCHAR(100)  NOT NULL,
    birth_date              DATE          NOT NULL,
    birth_place             VARCHAR(500)  NOT NULL,
    id_doc_type             VARCHAR(50)   NOT NULL,
    id_doc_series           VARCHAR(20),
    id_doc_number           VARCHAR(50)   NOT NULL,
    id_doc_issued_date      DATE          NOT NULL,
    id_doc_issued_by        VARCHAR(500)  NOT NULL,
    id_doc_department_code  VARCHAR(10),
    registration_address    TEXT          NOT NULL,
    residential_address     TEXT          NOT NULL,
    inn                     VARCHAR(12),
    email                   VARCHAR(255)  NOT NULL,
    phone                   VARCHAR(20)   NOT NULL,
    mgmt_company_name       VARCHAR(500),
    mgmt_company_ogrn       VARCHAR(13)
);

CREATE INDEX idx_investor_le_executives_entity ON investor_le_executives (investor_legal_entity_id);

--changeset investplatform:001-create-investor-document-types
CREATE TABLE investor_document_types (
    id   SERIAL       PRIMARY KEY,
    code VARCHAR(50)  NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO investor_document_types (code, name) VALUES
    ('passport_main',           'Паспорт — основная страница'),
    ('passport_registration',   'Паспорт — страница регистрации'),
    ('passport_all_pages',      'Паспорт — все страницы'),
    ('snils',                   'Страховое свидетельство (СНИЛС)'),
    ('ip_registration',         'Свидетельство о регистрации ИП / Лист записи ЕГРИП'),
    ('le_registration',         'Свидетельство о регистрации ЮЛ / Лист записи ЕГРЮЛ'),
    ('le_tax_registration',     'Свидетельство о постановке на учёт в налоговом органе'),
    ('le_charter',              'Устав в актуальной редакции'),
    ('le_executive_decision',   'Решение/протокол об избрании ЕИО'),
    ('le_tax_regime',           'Уведомление о виде налогообложения'),
    ('le_power_of_attorney',    'Доверенность представителя'),
    ('foreign_doc_translation', 'Перевод иностранного документа на русский язык'),
    ('other',                   'Иной документ');

--changeset investplatform:001-create-investor-documents
CREATE TABLE investor_documents (
    id               BIGSERIAL      PRIMARY KEY,
    investor_id      BIGINT         NOT NULL REFERENCES investors(id),
    document_type_id INTEGER        NOT NULL REFERENCES investor_document_types(id),
    report_year      SMALLINT       NOT NULL,
    file_name        VARCHAR(500)   NOT NULL,
    file_path        VARCHAR(1024)  NOT NULL,
    file_size        BIGINT,
    mime_type        VARCHAR(100),
    uploaded_at      TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_investor_documents_composite ON investor_documents (investor_id, document_type_id, report_year);
CREATE INDEX idx_investor_documents_type ON investor_documents (document_type_id);

--changeset investplatform:001-create-payments
CREATE TABLE payments (
    id                  BIGSERIAL      PRIMARY KEY,
    yukassa_payment_id  VARCHAR(50)    NOT NULL UNIQUE,
    personal_account_id BIGINT         NOT NULL REFERENCES personal_accounts(id),
    payment_type        VARCHAR(30)    NOT NULL CHECK (payment_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'COMMISSION')),
    direction           VARCHAR(10)    NOT NULL CHECK (direction IN ('INBOUND', 'OUTBOUND')),
    amount              DECIMAL(18,2)  NOT NULL CHECK (amount > 0),
    currency            VARCHAR(3)     NOT NULL DEFAULT 'RUB',
    yukassa_status      VARCHAR(50)    NOT NULL,
    payment_method_type VARCHAR(50)    NOT NULL,
    description         VARCHAR(500),
    yukassa_metadata    JSONB,
    idempotency_key     VARCHAR(255)   NOT NULL UNIQUE,
    receipt_url         VARCHAR(1024)  UNIQUE,
    paid_at             TIMESTAMP,
    canceled_at         TIMESTAMP,
    cancellation_reason VARCHAR(500),
    created_at          TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP      NOT NULL DEFAULT now(),
    version             BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_payments_account ON payments (personal_account_id);
CREATE INDEX idx_payments_status ON payments (yukassa_status);
CREATE INDEX idx_payments_created ON payments (created_at);
CREATE INDEX idx_payments_account_type ON payments (personal_account_id, payment_type);
CREATE INDEX idx_payments_account_created ON payments (personal_account_id, created_at);

--changeset investplatform:001-create-payment-refunds
CREATE TABLE payment_refunds (
    id                BIGSERIAL      PRIMARY KEY,
    yukassa_refund_id VARCHAR(50)    NOT NULL UNIQUE,
    payment_id        BIGINT         NOT NULL REFERENCES payments(id),
    amount            DECIMAL(18,2)  NOT NULL CHECK (amount > 0),
    yukassa_status    VARCHAR(50)    NOT NULL,
    reason            TEXT,
    idempotency_key   VARCHAR(255)   NOT NULL UNIQUE,
    receipt_url       VARCHAR(1024),
    created_at        TIMESTAMP      NOT NULL DEFAULT now(),
    refunded_at       TIMESTAMP,
    version           BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_payment_refunds_payment ON payment_refunds (payment_id);
CREATE INDEX idx_payment_refunds_status ON payment_refunds (yukassa_status);

--changeset investplatform:001-create-payouts
CREATE TABLE payouts (
    id                       BIGSERIAL      PRIMARY KEY,
    yukassa_payout_id        VARCHAR(50)    NOT NULL UNIQUE,
    personal_account_id      BIGINT         NOT NULL REFERENCES personal_accounts(id),
    payout_destination_type  VARCHAR(50)    NOT NULL,
    destination_details      JSONB,
    amount                   DECIMAL(18,2)  NOT NULL CHECK (amount > 0),
    yukassa_status           VARCHAR(50)    NOT NULL,
    description              VARCHAR(500),
    created_at               TIMESTAMP      NOT NULL DEFAULT now(),
    paid_at                  TIMESTAMP,
    canceled_at              TIMESTAMP,
    idempotency_key          VARCHAR(255)   NOT NULL UNIQUE,
    cancellation_reason      VARCHAR(500),
    version                  BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_payouts_account ON payouts (personal_account_id);
CREATE INDEX idx_payouts_status ON payouts (yukassa_status);
CREATE INDEX idx_payouts_created ON payouts (created_at);

--changeset investplatform:001-create-yukassa-webhooks
CREATE TABLE yukassa_webhooks (
    id                BIGSERIAL    PRIMARY KEY,
    event_id          VARCHAR(100) NOT NULL UNIQUE,
    event_type        VARCHAR(50)  NOT NULL,
    object_type       VARCHAR(30)  NOT NULL,
    object_id         VARCHAR(50)  NOT NULL,
    payload           JSONB        NOT NULL,
    processing_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING' CHECK (processing_status IN ('PENDING', 'PROCESSED', 'FAILED', 'SKIPPED')),
    error_message     TEXT,
    attempts          INTEGER      NOT NULL DEFAULT 0,
    processed_at      TIMESTAMP,
    created_at        TIMESTAMP    NOT NULL DEFAULT now(),
    version           BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_yukassa_webhooks_status_attempts ON yukassa_webhooks (processing_status, attempts);
CREATE INDEX idx_yukassa_webhooks_object ON yukassa_webhooks (object_type, object_id);
CREATE INDEX idx_yukassa_webhooks_event_type ON yukassa_webhooks (event_type);
CREATE INDEX idx_yukassa_webhooks_created ON yukassa_webhooks (created_at);

--changeset investplatform:001-create-yukassa-receipts
CREATE TABLE yukassa_receipts (
    id                      BIGSERIAL    PRIMARY KEY,
    yukassa_receipt_id      VARCHAR(50)  NOT NULL UNIQUE,
    receipt_type            VARCHAR(20)  NOT NULL CHECK (receipt_type IN ('PAYMENT', 'REFUND')),
    payment_id              BIGINT       REFERENCES payments(id),
    refund_id               BIGINT       REFERENCES payment_refunds(id),
    yukassa_status          VARCHAR(30)  NOT NULL,
    fiscal_document_number  VARCHAR(50),
    fiscal_storage_number   VARCHAR(50),
    fiscal_attribute        VARCHAR(50),
    registered_at           TIMESTAMP,
    receipt_url             VARCHAR(1024),
    payload                 JSONB,
    created_at              TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT now(),
    version                 BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_yukassa_receipts_payment ON yukassa_receipts (payment_id);
CREATE INDEX idx_yukassa_receipts_refund ON yukassa_receipts (refund_id);

ALTER TABLE yukassa_receipts ADD CONSTRAINT chk_receipt_ref
    CHECK (
        (receipt_type = 'PAYMENT' AND payment_id IS NOT NULL AND refund_id IS NULL) OR
        (receipt_type = 'REFUND'  AND refund_id IS NOT NULL AND payment_id IS NULL)
    );

--changeset investplatform:001-create-account-transactions
CREATE TABLE account_transactions (
    id                  BIGSERIAL      PRIMARY KEY,
    personal_account_id BIGINT         NOT NULL REFERENCES personal_accounts(id),
    transaction_type    VARCHAR(30)    NOT NULL CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_OUT', 'TRANSFER_IN', 'COMMISSION', 'HOLD', 'RELEASE')),
    amount              DECIMAL(18,2)  NOT NULL CHECK (amount > 0),
    balance_after       DECIMAL(18,2)  NOT NULL,
    payment_id          BIGINT         REFERENCES payments(id),
    payout_id           BIGINT         REFERENCES payouts(id),
    description         VARCHAR(500),
    created_at          TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_account_transactions_account_created ON account_transactions (personal_account_id, created_at);
CREATE INDEX idx_account_transactions_type ON account_transactions (transaction_type);
CREATE INDEX idx_account_transactions_payment ON account_transactions (payment_id);
CREATE INDEX idx_account_transactions_payout ON account_transactions (payout_id);

--changeset investplatform:001-create-security-classifications
CREATE TABLE security_classifications (
    id   BIGSERIAL    PRIMARY KEY,
    code VARCHAR(10)  NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE
);

--changeset investplatform:001-create-security-categories
CREATE TABLE security_categories (
    id   BIGSERIAL    PRIMARY KEY,
    code VARCHAR(10)  NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE
);

--changeset investplatform:001-create-securities
CREATE TABLE securities (
    id                         BIGSERIAL      PRIMARY KEY,
    securities_code            VARCHAR(50)    NOT NULL UNIQUE,
    security_classification_id BIGINT         NOT NULL REFERENCES security_classifications(id),
    security_category_id       BIGINT         NOT NULL REFERENCES security_categories(id),
    security_type              VARCHAR(100),
    state_reg_num              VARCHAR(20)    NOT NULL,
    state_reg_date             DATE           NOT NULL,
    isin                       VARCHAR(12)    NOT NULL UNIQUE,
    nominal_currency           VARCHAR(3)     NOT NULL DEFAULT 'RUB',
    nominal_value              DECIMAL(18,4)  NOT NULL CHECK (nominal_value > 0),
    quantity_in_issue          DECIMAL(18,4)  NOT NULL CHECK (quantity_in_issue > 0),
    quantity_placed            DECIMAL(18,4)  NOT NULL CHECK (quantity_placed >= 0),
    form_issue                 VARCHAR(20)    NOT NULL,
    cfi_code                   VARCHAR(6),
    financial_instrument_type  VARCHAR(10),
    emitent_id                 BIGINT         NOT NULL REFERENCES emitents(id),
    created_at                 TIMESTAMP      NOT NULL DEFAULT now(),
    version                    BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_securities_emitent ON securities (emitent_id);
CREATE INDEX idx_securities_classification_category ON securities (security_classification_id, security_category_id);
CREATE INDEX idx_securities_state_reg ON securities (state_reg_num);

ALTER TABLE securities ADD CONSTRAINT chk_placed_le_issue
    CHECK (quantity_placed <= quantity_in_issue);

--changeset investplatform:001-create-security-balances
CREATE TABLE security_balances (
    id                  BIGSERIAL      PRIMARY KEY,
    personal_account_id BIGINT         NOT NULL REFERENCES personal_accounts(id),
    security_id         BIGINT         NOT NULL REFERENCES securities(id),
    quantity            DECIMAL(18,4)  NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    quantity_blocked    DECIMAL(18,4)  NOT NULL DEFAULT 0 CHECK (quantity_blocked >= 0),
    quantity_pledged    DECIMAL(18,4)  NOT NULL DEFAULT 0 CHECK (quantity_pledged >= 0),
    quantity_encumbered DECIMAL(18,4)  NOT NULL DEFAULT 0 CHECK (quantity_encumbered >= 0),
    updated_at          TIMESTAMP      NOT NULL DEFAULT now(),
    version             BIGINT         NOT NULL DEFAULT 0,
    UNIQUE (personal_account_id, security_id)
);

CREATE INDEX idx_security_balances_security ON security_balances (security_id);

ALTER TABLE security_balances ADD CONSTRAINT chk_quantities_sum
    CHECK (quantity_blocked + quantity_pledged + quantity_encumbered <= quantity);

--changeset investplatform:001-create-registry-operation-types
CREATE TABLE registry_operation_types (
    id   BIGSERIAL    PRIMARY KEY,
    code VARCHAR(10)  NOT NULL UNIQUE,
    name VARCHAR(500) NOT NULL UNIQUE
);

INSERT INTO registry_operation_types (code, name) VALUES
    ('TRAN', 'Переход прав собственности при совершении сделки'),
    ('SUIN', 'Переход прав собственности в результате наследования'),
    ('REOR', 'Переход прав при реорганизации зарегистрированного лица'),
    ('JUDM', 'Переход прав по решению суда'),
    ('RTPL', 'Передача ЦБ при невыполнении условий залога'),
    ('TROT', 'Передача ЦБ по иным основаниям'),
    ('TRCL', 'Передача ЦБ вследствие отмены операции'),
    ('PLAC', 'Размещение ценных бумаг'),
    ('EMTR', 'Выкуп/приобретение ЦБ эмитентом'),
    ('EMRT', 'Возврат при несостоявшейся эмиссии'),
    ('EMDL', 'Списание ЦБ при реорганизации/ликвидации АО'),
    ('EMDR', 'Выкуп дробных акций эмитентом'),
    ('TEND', 'Выкуп ЦБ эмитентом по требованию акционеров'),
    ('RCAC', 'Зачисление ЦБ'),
    ('TRAC', 'Списание ЦБ'),
    ('RECE', 'Зачисление ЦБ со счёта номинального держателя'),
    ('DELI', 'Списание ЦБ со счёта номинального держателя'),
    ('PCST', 'Зачисление ЦБ при приёме реестра по состоянию'),
    ('CVRG', 'Конвертация ЦБ при реорганизации АО'),
    ('BLCK', 'Блокирование операций'),
    ('UBLK', 'Снятие блокировки'),
    ('PLDG', 'Залог ЦБ'),
    ('UPLD', 'Снятие залога'),
    ('APLD', 'Переуступка прав залога'),
    ('NPAY', 'Обременение оплатой'),
    ('UNPY', 'Снятие обременения оплатой'),
    ('ESCR', 'Операция эскроу'),
    ('OPEN', 'Открытие лицевого счёта'),
    ('CLOS', 'Закрытие лицевого счёта'),
    ('CHNG', 'Изменение анкетных данных'),
    ('NPTR', 'Зачисление не полностью оплаченных акций на счёт эмитента'),
    ('RCNL', 'Передача ЦБ на счёт неустановленных лиц'),
    ('TRNL', 'Передача ЦБ со счёта неустановленных лиц'),
    ('RCCD', 'Зачисление ЦБ на счёт НДЦД для размещения на торгах'),
    ('CDPC', 'Размещение ЦБ центральным депозитарием на торгах'),
    ('RCEM', 'Зачисление ЦБ на эмиссионный счёт эмитента'),
    ('CDRT', 'Возврат неразмещённых ЦБ со счёта центрального депозитария'),
    ('RCNT', 'Зачисление ЦБ на счёт нотариуса'),
    ('TRNT', 'Списание ЦБ со счёта нотариуса'),
    ('RCCM', 'Зачисление ЦБ на счёт доверительного управляющего'),
    ('TRCM', 'Списание ЦБ со счёта доверительного управляющего'),
    ('OTHR', 'Иная операция');

--changeset investplatform:001-create-contract-types
CREATE TABLE contract_types (
    id   BIGSERIAL    PRIMARY KEY,
    code VARCHAR(10)  NOT NULL UNIQUE,
    name VARCHAR(500) NOT NULL UNIQUE
);

--changeset investplatform:001-create-registry-operations
CREATE TABLE registry_operations (
    id                    BIGSERIAL      PRIMARY KEY,
    operation_type_id     BIGINT         NOT NULL REFERENCES registry_operation_types(id),
    operation_name        VARCHAR(500)   NOT NULL,
    operation_kind        VARCHAR(30)    CHECK (operation_kind IN ('TRANSACTION', 'OPERATION_FORM', 'EMISSION', 'BLOCKING', 'NONPAYMENT', 'PLEDGE', 'ASSIGNMENT_PLEDGE', 'ESCROW', 'OTHER')),
    processing_datetime   TIMESTAMP      NOT NULL,
    processing_reference  VARCHAR(100)   NOT NULL,
    date_state            DATE           NOT NULL,
    account_transfer_id   BIGINT         REFERENCES personal_accounts(id),
    account_receive_id    BIGINT         REFERENCES personal_accounts(id),
    security_id           BIGINT         REFERENCES securities(id),
    quantity              DECIMAL(18,4)  NOT NULL CHECK (quantity > 0),
    settlement_currency   VARCHAR(3)     NOT NULL,
    settlement_amount     DECIMAL(18,2)  NOT NULL CHECK (settlement_amount >= 0),
    content               TEXT,
    created_at            TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_registry_operations_type ON registry_operations (operation_type_id);
CREATE INDEX idx_registry_operations_security ON registry_operations (security_id);
CREATE INDEX idx_registry_operations_transfer ON registry_operations (account_transfer_id);
CREATE INDEX idx_registry_operations_receive ON registry_operations (account_receive_id);
CREATE INDEX idx_registry_operations_datetime ON registry_operations (processing_datetime);
CREATE INDEX idx_registry_operations_date_state ON registry_operations (date_state);

--changeset investplatform:001-create-registry-operation-documents
CREATE TABLE registry_operation_documents (
    id                    BIGSERIAL    PRIMARY KEY,
    registry_operation_id BIGINT       REFERENCES registry_operations(id),
    in_doc_num            VARCHAR(100) NOT NULL,
    in_reg_date           TIMESTAMP    NOT NULL,
    out_doc_num           VARCHAR(100),
    out_doc_date          TIMESTAMP
);

--changeset investplatform:001-create-registry-operation-basis
CREATE TABLE registry_operation_basis (
    id                    BIGSERIAL    PRIMARY KEY,
    registry_operation_id BIGINT       NOT NULL REFERENCES registry_operations(id),
    contract_type_id      BIGINT       NOT NULL REFERENCES contract_types(id),
    contract_narrative    VARCHAR(500),
    doc_num               VARCHAR(100) NOT NULL,
    doc_date              DATE         NOT NULL
);

--changeset investplatform:001-create-investment-methods
CREATE TABLE investment_methods (
    id   SERIAL       PRIMARY KEY,
    code VARCHAR(30)  NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO investment_methods (code, name) VALUES
    ('securities', 'Приобретение эмиссионных ценных бумаг');

--changeset investplatform:001-create-proposal-statuses
CREATE TABLE proposal_statuses (
    id          SERIAL       PRIMARY KEY,
    code        VARCHAR(30)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
);

INSERT INTO proposal_statuses (code, name, description) VALUES
    ('draft',     'Черновик',              'ИП создано, но не отправлено на проверку'),
    ('pending',   'Ожидает рассмотрения',  'ИП отправлено Оператору, ожидает рассмотрения'),
    ('reviewing', 'На рассмотрении',       'Оператор проверяет соответствие ИП требованиям закона и Правил'),
    ('rejected',  'Отклонено',             'ИП не прошло проверку Оператора'),
    ('active',    'Активно',               'ИП прошло проверку Оператора, доступно Инвесторам'),
    ('failed',    'Не состоялось',         'Условия сбора не выполнены, средства возвращены Инвесторам'),
    ('completed', 'Состоялось',            'Условия сбора выполнены, средства переведены Эмитенту');

--changeset investplatform:001-create-proposal-document-types
CREATE TABLE proposal_document_types (
    id          SERIAL       PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL UNIQUE,
    is_required BOOLEAN      NOT NULL DEFAULT FALSE
);

INSERT INTO proposal_document_types (code, name, is_required) VALUES
    ('financial_report',       'Годовая бухгалтерская (финансовая) отчётность',                         TRUE),
    ('audit_conclusion',       'Аудиторское заключение в отношении годовой бухгалтерской отчётности',     FALSE),
    ('issue_decision',         'Решение о выпуске ценных бумаг',                                        FALSE),
    ('placement_conditions',   'Документ, содержащий условия размещения ЦБ',                             FALSE),
    ('securities_prospectus',  'Проспект ценных бумаг',                                                 FALSE),
    ('draft_contract',         'Проект договора инвестирования',                                         TRUE),
    ('risk_warning',           'Предупреждение о рисках',                                               TRUE),
    ('other',                  'Иной документ',                                                         FALSE);

--changeset investplatform:001-create-investment-proposals
CREATE TABLE investment_proposals (
    id                           BIGSERIAL      PRIMARY KEY,
    emitent_id                   BIGINT         NOT NULL REFERENCES emitents(id),
    status_id                    INTEGER        NOT NULL REFERENCES proposal_statuses(id),
    investment_method_id         INTEGER        NOT NULL REFERENCES investment_methods(id),
    security_id                  BIGINT         REFERENCES securities(id),
    title                        VARCHAR(500)   NOT NULL,
    investment_goals             TEXT           NOT NULL,
    goal_risk_factors            TEXT           NOT NULL,
    emitent_risks                TEXT           NOT NULL,
    investment_risks             TEXT           NOT NULL,
    issue_decision_info          TEXT,
    placement_procedure          TEXT,
    placement_terms              TEXT,
    placement_conditions         TEXT,
    has_preemptive_right         BOOLEAN,
    preemptive_right_details     TEXT,
    risk_warning                 TEXT           NOT NULL,
    max_investment_amount        DECIMAL(18,2)  NOT NULL CHECK (max_investment_amount > 0),
    min_investment_amount        DECIMAL(18,2)  NOT NULL CHECK (min_investment_amount > 0),
    price_per_unit               DECIMAL(18,4)  NULL CHECK (price_per_unit > 0),
    total_quantity               BIGINT         NULL CHECK (total_quantity > 0),
    min_purchase_quantity        BIGINT         NULL CHECK (min_purchase_quantity > 0),
    max_purchase_quantity        BIGINT         NULL CHECK (max_purchase_quantity > 0),
    proposal_start_date          DATE           NOT NULL,
    proposal_end_date            DATE           NOT NULL,
    essential_contract_terms     TEXT           NOT NULL,
    expert_monitoring_info       TEXT,
    has_property_rights_condition BOOLEAN       NOT NULL DEFAULT FALSE,
    property_rights_details      TEXT,
    applicable_law               VARCHAR(500)  NOT NULL DEFAULT 'Российская Федерация',
    suspensive_conditions        TEXT,
    collected_amount             DECIMAL(18,2)  NOT NULL DEFAULT 0.00 CHECK (collected_amount >= 0),
    reviewed_by                  BIGINT         REFERENCES operators(id),
    rejection_reason             TEXT,
    created_at                   TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at                   TIMESTAMP      NOT NULL DEFAULT now(),
    submitted_at                 TIMESTAMP,
    reviewed_at                  TIMESTAMP,
    activated_at                 TIMESTAMP,
    closed_at                    TIMESTAMP,
    locked_by                    BIGINT         REFERENCES operators(id),
    lock_heartbeat_at            TIMESTAMP,
    version                      BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_investment_proposals_emitent ON investment_proposals (emitent_id);
CREATE INDEX idx_investment_proposals_status ON investment_proposals (status_id);
CREATE INDEX idx_investment_proposals_locked ON investment_proposals (locked_by) WHERE locked_by IS NOT NULL;
CREATE INDEX idx_investment_proposals_dates ON investment_proposals (proposal_start_date, proposal_end_date);
CREATE INDEX idx_investment_proposals_security ON investment_proposals (security_id);
CREATE INDEX idx_investment_proposals_created ON investment_proposals (created_at);

ALTER TABLE investment_proposals ADD CONSTRAINT chk_min_le_max
    CHECK (min_investment_amount <= max_investment_amount);
ALTER TABLE investment_proposals ADD CONSTRAINT chk_collected_le_max
    CHECK (collected_amount <= max_investment_amount);
ALTER TABLE investment_proposals ADD CONSTRAINT chk_dates
    CHECK (proposal_end_date > proposal_start_date);
ALTER TABLE investment_proposals ADD CONSTRAINT chk_min_purchase_le_max_purchase
    CHECK (min_purchase_quantity <= max_purchase_quantity);
ALTER TABLE investment_proposals ADD CONSTRAINT chk_max_purchase_le_total
    CHECK (max_purchase_quantity <= total_quantity);

--changeset investplatform:001-create-proposal-status-history
CREATE TABLE proposal_status_history (
    id            BIGSERIAL PRIMARY KEY,
    proposal_id   BIGINT    NOT NULL REFERENCES investment_proposals(id),
    old_status_id INTEGER   REFERENCES proposal_statuses(id),
    new_status_id INTEGER   NOT NULL REFERENCES proposal_statuses(id),
    changed_by    BIGINT    NOT NULL REFERENCES users(id),
    comment       TEXT,
    created_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_proposal_status_history_proposal ON proposal_status_history (proposal_id, created_at);
CREATE INDEX idx_proposal_status_history_changed_by ON proposal_status_history (changed_by);

--changeset investplatform:001-create-proposal-documents
CREATE TABLE proposal_documents (
    id               BIGSERIAL      PRIMARY KEY,
    proposal_id      BIGINT         NOT NULL REFERENCES investment_proposals(id),
    document_type_id INTEGER        NOT NULL REFERENCES proposal_document_types(id),
    file_name        VARCHAR(500)   NOT NULL,
    file_path        VARCHAR(1024)  NOT NULL,
    file_size        BIGINT,
    mime_type        VARCHAR(100),
    uploaded_at      TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_proposal_documents_composite ON proposal_documents (proposal_id, document_type_id);
CREATE INDEX idx_proposal_documents_type ON proposal_documents (document_type_id);

--changeset investplatform:001-create-contract-statuses
CREATE TABLE contract_statuses (
    id          SERIAL       PRIMARY KEY,
    code        VARCHAR(30)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
);

INSERT INTO contract_statuses (code, name, description) VALUES
    ('reviewing',  'На рассмотрении', 'ДИ оплачен и отправлен Оператору'),
    ('withdrawn',  'Отозван',         'ДИ отозван по инициативе Инвестора. Средства возвращены на счёт Инвестора'),
    ('rejected',   'Отклонен',        'ДИ не прошёл проверку Оператора ИЛИ Оператор не приступал к рассмотрению'),
    ('approved',   'Одобрен',         'ДИ прошёл проверку Оператора. Ожидание окончания сбора'),
    ('completed',  'Состоялся',       'Условия сбора выполнены, ДИ заключён. Средства переведены на счёт Эмитента'),
    ('failed',     'Не состоялся',    'Условия сбора не выполнены, ДИ не заключён. Средства возвращены на счёт Инвестора');

--changeset investplatform:001-create-investment-contracts
CREATE TABLE investment_contracts (
    id                  BIGSERIAL      PRIMARY KEY,
    contract_number     VARCHAR(100)   NOT NULL UNIQUE,
    proposal_id         BIGINT         NOT NULL REFERENCES investment_proposals(id),
    investor_id         BIGINT         NOT NULL REFERENCES investors(id),
    status_id           INTEGER        NOT NULL REFERENCES contract_statuses(id),
    amount              DECIMAL(18,2)  NOT NULL CHECK (amount > 0),
    security_id         BIGINT         REFERENCES securities(id),
    securities_quantity DECIMAL(18,4)  CHECK (securities_quantity > 0),
    price_per_security  DECIMAL(18,4)  CHECK (price_per_security > 0),
    commission_amount   DECIMAL(18,2)  NOT NULL DEFAULT 0.00 CHECK (commission_amount >= 0),
    reviewed_by         BIGINT         REFERENCES operators(id),
    rejection_reason    TEXT,
    withdrawal_reason   TEXT,
    payment_id          BIGINT         REFERENCES payments(id),
    refund_id           BIGINT         REFERENCES payment_refunds(id),
    signed_at           TIMESTAMP,
    reviewed_at         TIMESTAMP,
    withdrawn_at        TIMESTAMP,
    completed_at        TIMESTAMP,
    failed_at           TIMESTAMP,
    created_at          TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP      NOT NULL DEFAULT now(),
    locked_by           BIGINT         REFERENCES operators(id),
    lock_heartbeat_at   TIMESTAMP,
    version             BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_investment_contracts_proposal ON investment_contracts (proposal_id);
CREATE INDEX idx_investment_contracts_investor ON investment_contracts (investor_id);
CREATE INDEX idx_investment_contracts_status ON investment_contracts (status_id);
CREATE INDEX idx_investment_contracts_locked ON investment_contracts (locked_by) WHERE locked_by IS NOT NULL;
CREATE INDEX idx_investment_contracts_proposal_status ON investment_contracts (proposal_id, status_id);
CREATE INDEX idx_investment_contracts_investor_created ON investment_contracts (investor_id, created_at);
CREATE INDEX idx_investment_contracts_payment ON investment_contracts (payment_id);

ALTER TABLE investment_contracts
    ADD CONSTRAINT uq_investor_proposal UNIQUE (proposal_id, investor_id);

--changeset investplatform:001-create-contract-status-history
CREATE TABLE contract_status_history (
    id            BIGSERIAL PRIMARY KEY,
    contract_id   BIGINT    NOT NULL REFERENCES investment_contracts(id),
    old_status_id INTEGER   REFERENCES contract_statuses(id),
    new_status_id INTEGER   NOT NULL REFERENCES contract_statuses(id),
    changed_by    BIGINT    NOT NULL REFERENCES users(id),
    comment       TEXT,
    created_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_contract_status_history_contract ON contract_status_history (contract_id, created_at);
CREATE INDEX idx_contract_status_history_changed_by ON contract_status_history (changed_by);

--changeset investplatform:001-create-auth-audit-log
CREATE TABLE auth_audit_log (
    id            BIGSERIAL    PRIMARY KEY,
    user_id       BIGINT       REFERENCES users(id),
    event_type    VARCHAR(50)  NOT NULL,
    ip_address    VARCHAR(45)  NOT NULL,
    user_agent    VARCHAR(1000),
    session_id    VARCHAR(255),
    details       JSONB,
    is_successful BOOLEAN      NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_auth_audit_user_id ON auth_audit_log (user_id);
CREATE INDEX idx_auth_audit_event_type ON auth_audit_log (event_type);
CREATE INDEX idx_auth_audit_created_at ON auth_audit_log (created_at);
CREATE INDEX idx_auth_audit_ip ON auth_audit_log (ip_address);

--changeset investplatform:001-create-document-audit-log
CREATE TABLE document_audit_log (
    id          BIGSERIAL      PRIMARY KEY,
    user_id     BIGINT         NOT NULL REFERENCES users(id),
    entity_type VARCHAR(50)    NOT NULL,
    entity_id   BIGINT         NOT NULL,
    document_id BIGINT         NOT NULL,
    action      VARCHAR(20)    NOT NULL,
    file_name   VARCHAR(500)   NOT NULL,
    file_path   VARCHAR(1024)  NOT NULL,
    file_hash   VARCHAR(128),
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_doc_audit_entity ON document_audit_log (entity_type, entity_id);
CREATE INDEX idx_doc_audit_user ON document_audit_log (user_id);
CREATE INDEX idx_doc_audit_created ON document_audit_log (created_at);

--changeset investplatform:001-create-data-audit-log
CREATE TABLE data_audit_log (
    id             BIGSERIAL    PRIMARY KEY,
    table_name     VARCHAR(100) NOT NULL,
    record_id      BIGINT       NOT NULL,
    action         VARCHAR(10)  NOT NULL,
    old_values     JSONB,
    new_values     JSONB,
    changed_fields TEXT[],
    db_user        VARCHAR(100) NOT NULL,
    app_user_id    BIGINT,
    ip_address     VARCHAR(45),
    transaction_id BIGINT       NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_data_audit_table_record ON data_audit_log (table_name, record_id);
CREATE INDEX idx_data_audit_created ON data_audit_log (created_at);
CREATE INDEX idx_data_audit_app_user ON data_audit_log (app_user_id);
CREATE INDEX idx_data_audit_action ON data_audit_log (action);

--changeset investplatform:001-create-data-access-log
CREATE TABLE data_access_log (
    id          BIGSERIAL    PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users(id),
    entity_type VARCHAR(50)  NOT NULL,
    entity_id   BIGINT       NOT NULL,
    access_type VARCHAR(30)  NOT NULL,
    ip_address  VARCHAR(45)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);

--changeset investplatform:001-create-business-audit-log
CREATE TABLE business_audit_log (
    id             BIGSERIAL      PRIMARY KEY,
    user_id        BIGINT         NOT NULL REFERENCES users(id),
    action         VARCHAR(100)   NOT NULL,
    entity_type    VARCHAR(50)    NOT NULL,
    entity_id      BIGINT         NOT NULL,
    request_method VARCHAR(10),
    request_uri    VARCHAR(1024),
    ip_address     VARCHAR(45)    NOT NULL,
    user_agent     VARCHAR(1000),
    input_params   JSONB,
    result         VARCHAR(20)    NOT NULL,
    error_message  TEXT,
    duration_ms    INTEGER,
    created_at     TIMESTAMP      NOT NULL DEFAULT now()
);

--changeset investplatform:001-create-pd-consent-types
CREATE TABLE pd_consent_types (
    id          SERIAL       PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_required BOOLEAN      NOT NULL DEFAULT TRUE
);

INSERT INTO pd_consent_types (code, name, description, is_required) VALUES
    ('registration',        'Согласие при регистрации',
     'Обработка ПД для создания учётной записи, идентификации и аутентификации на платформе', TRUE),
    ('investment_activity',  'Согласие на обработку ПД в рамках инвестиционной деятельности',
     'Обработка ПД для заключения и исполнения договоров инвестирования, ведения реестра владельцев ЦБ', TRUE),
    ('regulatory_reporting', 'Согласие на передачу ПД регулятору',
     'Передача ПД в Банк России и иные уполномоченные органы в соответствии с 259-ФЗ', TRUE),
    ('marketing',            'Согласие на маркетинговые коммуникации',
     'Рассылка информационных и рекламных материалов по email и SMS', FALSE),
    ('cross_border',         'Согласие на трансграничную передачу ПД',
     'Передача ПД в юрисдикции, обеспечивающие адекватную защиту прав субъектов ПД', FALSE);

--changeset investplatform:001-create-pd-consent-versions
CREATE TABLE pd_consent_versions (
    id               BIGSERIAL PRIMARY KEY,
    consent_type_id  INTEGER   NOT NULL REFERENCES pd_consent_types(id),
    version_number   INTEGER   NOT NULL,
    content          TEXT      NOT NULL,
    effective_from   DATE      NOT NULL,
    effective_to     DATE,
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (consent_type_id, version_number)
);

CREATE INDEX idx_pd_consent_versions_type_from ON pd_consent_versions (consent_type_id, effective_from);

--changeset investplatform:001-create-pd-consents
CREATE TABLE pd_consents (
    id                 BIGSERIAL    PRIMARY KEY,
    user_id            BIGINT       NOT NULL REFERENCES users(id),
    consent_version_id BIGINT       NOT NULL REFERENCES pd_consent_versions(id),
    status             VARCHAR(20)  NOT NULL CHECK (status IN ('ACTIVE', 'REVOKED', 'EXPIRED')),
    accepted_at        TIMESTAMP    NOT NULL,
    revoked_at         TIMESTAMP,
    revocation_method  VARCHAR(50),
    expires_at         TIMESTAMP,
    ip_address         VARCHAR(45)  NOT NULL,
    user_agent         VARCHAR(1000),
    created_at         TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP    NOT NULL DEFAULT now(),
    version            BIGINT       NOT NULL DEFAULT 0,
    UNIQUE (user_id, consent_version_id)
);

CREATE INDEX idx_pd_consents_user_status ON pd_consents (user_id, status);
CREATE INDEX idx_pd_consents_status ON pd_consents (status);

ALTER TABLE pd_consents ADD CONSTRAINT chk_revoked_date
    CHECK (status <> 'REVOKED' OR revoked_at IS NOT NULL);
ALTER TABLE pd_consents ADD CONSTRAINT chk_revoked_after_accepted
    CHECK (revoked_at IS NULL OR revoked_at >= accepted_at);
ALTER TABLE pd_consents ADD CONSTRAINT chk_expires_after_accepted
    CHECK (expires_at IS NULL OR expires_at >= accepted_at);

--changeset investplatform:001-create-notification-channels
CREATE TABLE notification_channels (
    id   SERIAL       PRIMARY KEY,
    code VARCHAR(30)  NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO notification_channels (code, name) VALUES
    ('web',   'Веб-интерфейс (in-app)'),
    ('email', 'Электронная почта'),
    ('sms',   'SMS'),
    ('push',  'Push-уведомление');

--changeset investplatform:001-create-notification-event-types
CREATE TABLE notification_event_types (
    id               SERIAL       PRIMARY KEY,
    code             VARCHAR(80)  NOT NULL UNIQUE,
    name             VARCHAR(255) NOT NULL UNIQUE,
    description      TEXT,
    default_channels INTEGER[]
);

INSERT INTO notification_event_types (code, name, description) VALUES
    ('proposal_submitted',        'ИП отправлено на проверку',           'Эмитент отправил ИП оператору'),
    ('proposal_approved',         'ИП одобрено',                        'Оператор одобрил ИП'),
    ('proposal_rejected',         'ИП отклонено',                       'Оператор отклонил ИП'),
    ('proposal_activated',        'ИП опубликовано',                    'ИП стало доступно для инвесторов'),
    ('proposal_completed',        'Сбор по ИП завершён успешно',        'Условия сбора выполнены'),
    ('proposal_failed',           'Сбор по ИП не состоялся',            'Условия сбора не выполнены'),
    ('contract_created',          'ДИ создан',                          'Инвестор оформил ДИ'),
    ('contract_approved',         'ДИ одобрен оператором',              'Оператор одобрил ДИ'),
    ('contract_rejected',         'ДИ отклонён оператором',             'Оператор отклонил ДИ'),
    ('contract_withdrawn',        'ДИ отозван инвестором',              'Инвестор отозвал ДИ'),
    ('contract_completed',        'ДИ исполнен',                        'ДИ заключён, средства переведены'),
    ('contract_failed',           'ДИ не состоялся',                    'Средства возвращены инвестору'),
    ('payment_received',          'Платёж получен',                     'Средства зачислены на лицевой счёт'),
    ('payout_completed',          'Выплата выполнена',                  'Средства переведены на внешний счёт'),
    ('payout_failed',             'Выплата не выполнена',               'Ошибка при выполнении выплаты'),
    ('refund_completed',          'Возврат выполнен',                   'Средства возвращены'),
    ('password_changed',          'Пароль изменён',                     'Пароль учётной записи был изменён'),
    ('login_from_new_device',     'Вход с нового устройства',           'Зафиксирован вход с неизвестного устройства'),
    ('account_locked',            'Аккаунт заблокирован',               'Аккаунт заблокирован после неудачных попыток входа'),
    ('consent_expiring_soon',     'Согласие на обработку ПД истекает',  'Срок действия согласия истекает через 30 дней'),
    ('verification_required',     'Требуется верификация',              'Необходимо пройти проверку документов'),
    ('verification_completed',    'Верификация пройдена',               'Проверка документов завершена');

--changeset investplatform:001-create-notification-templates
CREATE TABLE notification_templates (
    id               BIGSERIAL    PRIMARY KEY,
    event_type_id    INTEGER      NOT NULL REFERENCES notification_event_types(id),
    channel_id       INTEGER      NOT NULL REFERENCES notification_channels(id),
    locale           VARCHAR(10)  NOT NULL DEFAULT 'ru',
    subject_template VARCHAR(500),
    body_template    TEXT         NOT NULL,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT now(),
    UNIQUE (event_type_id, channel_id, locale)
);

--changeset investplatform:001-create-notification-preferences
CREATE TABLE notification_preferences (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT    NOT NULL REFERENCES users(id),
    event_type_id INTEGER   NOT NULL REFERENCES notification_event_types(id),
    channel_id    INTEGER   NOT NULL REFERENCES notification_channels(id),
    is_enabled    BOOLEAN   NOT NULL DEFAULT TRUE,
    updated_at    TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (user_id, event_type_id, channel_id)
);

CREATE INDEX idx_notification_preferences_user ON notification_preferences (user_id);

--changeset investplatform:001-create-notifications
CREATE TABLE notifications (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users(id),
    event_type_id   INTEGER      NOT NULL REFERENCES notification_event_types(id),
    channel_id      INTEGER      NOT NULL REFERENCES notification_channels(id),
    entity_type     VARCHAR(50),
    entity_id       BIGINT,
    title           VARCHAR(500) NOT NULL,
    body            TEXT         NOT NULL,
    delivery_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING' CHECK (delivery_status IN ('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'SKIPPED')),
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at         TIMESTAMP,
    sent_at         TIMESTAMP,
    delivered_at    TIMESTAMP,
    failure_reason  VARCHAR(500),
    retry_count     INTEGER      NOT NULL DEFAULT 0,
    next_retry_at   TIMESTAMP,
    metadata        JSONB,
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now(),
    version         BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_notifications_user_unread ON notifications (user_id, is_read, created_at);
CREATE INDEX idx_notifications_user_event ON notifications (user_id, event_type_id, created_at);
CREATE INDEX idx_notifications_delivery_retry ON notifications (delivery_status, next_retry_at);
CREATE INDEX idx_notifications_entity ON notifications (entity_type, entity_id);
CREATE INDEX idx_notifications_created ON notifications (created_at);

ALTER TABLE notifications ADD CONSTRAINT chk_read_at
    CHECK (is_read = FALSE OR read_at IS NOT NULL);
ALTER TABLE notifications ADD CONSTRAINT chk_sent_at
    CHECK (delivery_status = 'PENDING' OR delivery_status = 'SKIPPED' OR sent_at IS NOT NULL);

--changeset investplatform:001-create-personal-accounts-constraint
ALTER TABLE personal_accounts ADD CONSTRAINT chk_balance_hold
    CHECK (balance >= hold_amount);

--changeset investplatform:001-create-emitent-yearly-limits
ALTER TABLE emitent_legal_entities ADD CONSTRAINT chk_yearly_limit
    CHECK (invested_current_year <= 1000000000.00);
ALTER TABLE emitent_private_entrepreneurs ADD CONSTRAINT chk_yearly_limit
    CHECK (invested_current_year <= 1000000000.00);

--changeset investplatform:001-create-partial-indexes
CREATE INDEX idx_proposals_active ON investment_proposals (id)
    WHERE status_id = 5; -- proposal_statuses: 'active'

CREATE INDEX idx_webhooks_pending ON yukassa_webhooks (created_at)
    WHERE processing_status = 'PENDING';

CREATE INDEX idx_notifications_pending ON notifications (created_at)
    WHERE delivery_status = 'PENDING';

CREATE INDEX idx_notifications_retry ON notifications (next_retry_at)
    WHERE delivery_status = 'FAILED' AND next_retry_at IS NOT NULL;

--changeset investplatform:001-create-audit-trigger-function runOnChange:true stripComments:false splitStatements:false
CREATE OR REPLACE FUNCTION audit_trigger_func()
RETURNS TRIGGER AS $$
DECLARE
    _old_values  JSONB := NULL;
    _new_values  JSONB := NULL;
    _changed     TEXT[] := '{}';
    _record_id   BIGINT;
    _app_user_id BIGINT;
    _ip          VARCHAR(45);
    _key         TEXT;
BEGIN
    BEGIN
        _app_user_id := current_setting('app.current_user_id', TRUE)::BIGINT;
    EXCEPTION WHEN OTHERS THEN
        _app_user_id := NULL;
    END;

    BEGIN
        _ip := current_setting('app.current_ip', TRUE);
    EXCEPTION WHEN OTHERS THEN
        _ip := NULL;
    END;

    IF (TG_OP = 'DELETE') THEN
        _old_values := to_jsonb(OLD);
        _record_id  := COALESCE((_old_values->>'id')::BIGINT, 0);
    ELSIF (TG_OP = 'INSERT') THEN
        _new_values := to_jsonb(NEW);
        _record_id  := COALESCE((_new_values->>'id')::BIGINT, 0);
    ELSIF (TG_OP = 'UPDATE') THEN
        _old_values := to_jsonb(OLD);
        _new_values := to_jsonb(NEW);
        _record_id  := COALESCE((_new_values->>'id')::BIGINT, 0);

        FOR _key IN SELECT jsonb_object_keys(_new_values)
        LOOP
            IF (_old_values -> _key) IS DISTINCT FROM (_new_values -> _key) THEN
                _changed := array_append(_changed, _key);
            END IF;
        END LOOP;

        IF array_length(_changed, 1) IS NULL THEN
            RETURN NEW;
        END IF;
    END IF;

    INSERT INTO data_audit_log (
        table_name, record_id, action,
        old_values, new_values, changed_fields,
        db_user, app_user_id, ip_address,
        transaction_id, created_at
    ) VALUES (
        TG_TABLE_NAME, _record_id, TG_OP,
        _old_values, _new_values, _changed,
        current_user, _app_user_id, _ip,
        txid_current(), now()
    );

    IF (TG_OP = 'DELETE') THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

--changeset investplatform:001-create-audit-triggers
CREATE TRIGGER trg_audit_personal_accounts
    AFTER INSERT OR UPDATE OR DELETE ON personal_accounts
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_payments
    AFTER INSERT OR UPDATE OR DELETE ON payments
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_payment_refunds
    AFTER INSERT OR UPDATE OR DELETE ON payment_refunds
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_payouts
    AFTER INSERT OR UPDATE OR DELETE ON payouts
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_account_transactions
    AFTER INSERT OR UPDATE OR DELETE ON account_transactions
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_investment_proposals
    AFTER INSERT OR UPDATE OR DELETE ON investment_proposals
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_investment_contracts
    AFTER INSERT OR UPDATE OR DELETE ON investment_contracts
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_securities
    AFTER INSERT OR UPDATE OR DELETE ON securities
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_security_balances
    AFTER INSERT OR UPDATE OR DELETE ON security_balances
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_registry_operations
    AFTER INSERT OR UPDATE OR DELETE ON registry_operations
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_users
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_pd_consents
    AFTER INSERT OR UPDATE OR DELETE ON pd_consents
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_notifications
    AFTER INSERT OR UPDATE OR DELETE ON notifications
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

CREATE TRIGGER trg_audit_pd_consent_types
    AFTER INSERT OR UPDATE OR DELETE ON pd_consent_types
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_func();

--changeset investplatform:001-create-expire-pd-consents-function runOnChange:true stripComments:false splitStatements:false
CREATE OR REPLACE FUNCTION expire_pd_consents()
RETURNS void AS $$
BEGIN
    UPDATE pd_consents
    SET    status     = 'EXPIRED',
           updated_at = now()
    WHERE  status     = 'ACTIVE'
      AND  expires_at IS NOT NULL
      AND  expires_at < now();
END;
$$ LANGUAGE plpgsql;
