--liquibase formatted sql

-- ============================================================
-- MOCK DATA FOR TESTING ALL USER ROLES
-- Password for all users: Test1234!
-- ============================================================

--changeset investplatform:002-mock-users context:dev
-- BCrypt hash of "Test1234!"
-- Roles: 1=ROLE_ADMIN, 2=ROLE_OPERATOR, 3=ROLE_EMITENT, 4=ROLE_INVESTOR

-- Operators
INSERT INTO users (id, email, password_hash, role_id, is_enabled, is_account_non_locked, is_2fa_enabled) VALUES
    (100, 'operator1@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 2, TRUE, TRUE, FALSE),
    (101, 'operator2@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 2, TRUE, TRUE, FALSE);

-- Emitents
INSERT INTO users (id, email, password_hash, role_id, is_enabled, is_account_non_locked, is_2fa_enabled) VALUES
    (200, 'emitent.ooo@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 3, TRUE, TRUE, FALSE),
    (201, 'emitent.ip@investplatform.ru',  '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 3, TRUE, TRUE, FALSE);

-- Investors
INSERT INTO users (id, email, password_hash, role_id, is_enabled, is_account_non_locked, is_2fa_enabled) VALUES
    (300, 'investor.fl@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (301, 'investor.ip@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (302, 'investor.ul@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE);

-- Reset sequence
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

--changeset investplatform:002-mock-operators
INSERT INTO operators (id, user_id, last_name, first_name, patronymic) VALUES
    (100, 100, 'Смирнова', 'Анна', 'Владимировна'),
    (101, 101, 'Козлов',   'Дмитрий', 'Сергеевич');

SELECT setval('operators_id_seq', (SELECT MAX(id) FROM operators));

--changeset investplatform:002-mock-personal-accounts
-- Emitent accounts (account_type 01 = Счёт владельца)
INSERT INTO personal_accounts (id, account_number, account_type_id, balance, hold_amount) VALUES
    (200, 'EM-2024-000001', 1, 1500000.00, 0.00),
    (201, 'EM-2024-000002', 1,  350000.00, 0.00);

-- Investor accounts
INSERT INTO personal_accounts (id, account_number, account_type_id, balance, hold_amount) VALUES
    (300, 'IN-2024-000001', 1, 500000.00,  50000.00),
    (301, 'IN-2024-000002', 1, 250000.00,       0.00),
    (302, 'IN-2024-000003', 1, 3000000.00, 200000.00);

SELECT setval('personal_accounts_id_seq', (SELECT MAX(id) FROM personal_accounts));

--changeset investplatform:002-mock-emitents
INSERT INTO emitents (id, user_id, emitent_type, personal_account_id) VALUES
    (200, 200, 'LEGAL_ENTITY',         200),
    (201, 201, 'PRIVATE_ENTREPRENEUR', 201);

SELECT setval('emitents_id_seq', (SELECT MAX(id) FROM emitents));

--changeset investplatform:002-mock-emitent-legal-entity
INSERT INTO emitent_legal_entities (id, emitent_id, full_name, short_name, ogrn, inn, kpp, legal_address, postal_address, okpo, okato, organisation_form, material_facts, invested_current_year) VALUES
    (200, 200,
     'Общество с ограниченной ответственностью "ТехноИнвест"',
     'ООО "ТехноИнвест"',
     '1027700132195',
     '7707083893',
     '770701001',
     'г. Москва, ул. Ленина, д. 42, оф. 301',
     'г. Москва, ул. Ленина, д. 42, оф. 301',
     '08697140',
     '45286580000',
     'ООО',
     'Компания специализируется на разработке программного обеспечения для финансового сектора. Выручка за 2025 год составила 85 млн руб. Чистая прибыль — 12 млн руб.',
     5000000.00);

SELECT setval('emitent_legal_entities_id_seq', (SELECT MAX(id) FROM emitent_legal_entities));

--changeset investplatform:002-mock-emitent-pe
INSERT INTO emitent_private_entrepreneurs (id, emitent_id, last_name, first_name, patronymic, birth_date, birth_place, ogrnip, inn, registration_address, snils, material_facts, invested_current_year) VALUES
    (201, 201,
     'Петров', 'Алексей', 'Николаевич',
     '1985-03-15',
     'г. Санкт-Петербург',
     '318784700116537',
     '780125467890',
     'г. Санкт-Петербург, пр. Невский, д. 100, кв. 15',
     '12345678901',
     'ИП Петров А.Н. занимается производством биоразлагаемой упаковки. Годовой оборот — 18 млн руб.',
     2000000.00);

SELECT setval('emitent_private_entrepreneurs_id_seq', (SELECT MAX(id) FROM emitent_private_entrepreneurs));

--changeset investplatform:002-mock-emitent-le-shareholders
INSERT INTO emitent_le_shareholders (id, emitent_legal_entity_id, person_type, full_name, country, inn, ogrn, vote_share_percent, ownership_basis) VALUES
    (1, 200, 'INDIVIDUAL',  'Иванов Сергей Петрович',                  'Россия', '770812345678', NULL,            60.00, 'Учредительный договор от 01.01.2020'),
    (2, 200, 'LEGAL_ENTITY', 'ООО "Стартап Венчурс"',                   'Россия', '7709456123',   '1177746123456', 40.00, 'Договор купли-продажи доли от 15.06.2022');

SELECT setval('emitent_le_shareholders_id_seq', (SELECT MAX(id) FROM emitent_le_shareholders));

--changeset investplatform:002-mock-emitent-le-governing
INSERT INTO emitent_le_governing_bodies (id, emitent_legal_entity_id, body_name) VALUES
    (1, 200, 'Общее собрание участников'),
    (2, 200, 'Генеральный директор');

INSERT INTO emitent_le_governing_members (id, governing_body_id, person_type, full_name, country, inn) VALUES
    (1, 1, 'INDIVIDUAL', 'Иванов Сергей Петрович', 'Россия', '770812345678'),
    (2, 2, 'INDIVIDUAL', 'Иванов Сергей Петрович', 'Россия', '770812345678');

SELECT setval('emitent_le_governing_bodies_id_seq', (SELECT MAX(id) FROM emitent_le_governing_bodies));
SELECT setval('emitent_le_governing_members_id_seq', (SELECT MAX(id) FROM emitent_le_governing_members));

--changeset investplatform:002-mock-emitent-le-minority
INSERT INTO emitent_le_minority_info (id, emitent_legal_entity_id, total_shares_count, total_share_percent) VALUES
    (1, 200, 0, 0.00);

SELECT setval('emitent_le_minority_info_id_seq', (SELECT MAX(id) FROM emitent_le_minority_info));

--changeset investplatform:002-mock-okveds
INSERT INTO okveds (id, code, name) VALUES
    (1, '62.01', 'Разработка компьютерного программного обеспечения'),
    (2, '62.02', 'Деятельность консультативная и работы в области компьютерных технологий'),
    (3, '22.22', 'Производство пластмассовых изделий для упаковывания товаров'),
    (4, '46.76', 'Торговля оптовая прочими промежуточными продуктами');

SELECT setval('okveds_id_seq', (SELECT MAX(id) FROM okveds));

--changeset investplatform:002-mock-emitent-okved
INSERT INTO emitent_okved (emitent_id, okved_id, is_primary) VALUES
    (200, 1, TRUE),
    (200, 2, FALSE),
    (201, 3, TRUE),
    (201, 4, FALSE);

--changeset investplatform:002-mock-investors
INSERT INTO investors (id, user_id, investor_type, personal_account_id, is_qualified, qualified_at, qualified_basis, risk_declaration_accepted, risk_accepted_at) VALUES
    (300, 300, 'INDIVIDUAL',            300, FALSE, NULL,                       NULL,                          TRUE,  '2025-06-01 10:00:00'),
    (301, 301, 'PRIVATE_ENTREPRENEUR',  301, FALSE, NULL,                       NULL,                          TRUE,  '2025-07-15 14:30:00'),
    (302, 302, 'LEGAL_ENTITY',          302, TRUE,  '2025-05-20 09:00:00',      'Квалифицированный инвестор по п.2 ст.51.2 ФЗ-39', TRUE, '2025-05-20 09:00:00');

SELECT setval('investors_id_seq', (SELECT MAX(id) FROM investors));

--changeset investplatform:002-mock-investor-individual
INSERT INTO investor_individuals (id, investor_id, last_name, first_name, patronymic, gender, citizenship, birth_date, birth_place, id_doc_type, id_doc_series, id_doc_number, id_doc_issued_date, id_doc_issued_by, id_doc_department_code, registration_address, residential_address, inn, snils, email, phone, invested_other_platforms) VALUES
    (300, 300,
     'Сидорова', 'Елена', 'Александровна',
     'FEMALE',
     'Российская Федерация',
     '1990-08-25',
     'г. Москва',
     'Паспорт гражданина РФ',
     '4520', '654321',
     '2015-04-10',
     'Отделением УФМС России по г. Москве по району Тверской',
     '770-120',
     'г. Москва, ул. Пушкина, д. 10, кв. 45',
     'г. Москва, ул. Пушкина, д. 10, кв. 45',
     '772345678901',
     '123-456-789 01',
     'investor.fl@investplatform.ru',
     '+79161234567',
     150000.00);

SELECT setval('investor_individuals_id_seq', (SELECT MAX(id) FROM investor_individuals));

--changeset investplatform:002-mock-investor-pe
INSERT INTO investor_private_entrepreneurs (id, investor_id, last_name, first_name, patronymic, gender, citizenship, birth_date, birth_place, id_doc_type, id_doc_series, id_doc_number, id_doc_issued_date, id_doc_issued_by, id_doc_department_code, registration_address, residential_address, inn, snils, ogrnip, email, phone) VALUES
    (301, 301,
     'Волков', 'Игорь', 'Дмитриевич',
     'MALE',
     'Российская Федерация',
     '1988-11-03',
     'г. Екатеринбург',
     'Паспорт гражданина РФ',
     '6510', '987654',
     '2018-09-20',
     'ТП №3 ОУФМС России по Свердловской обл. в Ленинском р-не г. Екатеринбурга',
     '660-003',
     'г. Екатеринбург, ул. Малышева, д. 56, кв. 12',
     'г. Екатеринбург, ул. Малышева, д. 56, кв. 12',
     '667890123456',
     '987-654-321 09',
     '319665800012345',
     'investor.ip@investplatform.ru',
     '+79221234567');

SELECT setval('investor_private_entrepreneurs_id_seq', (SELECT MAX(id) FROM investor_private_entrepreneurs));

--changeset investplatform:002-mock-investor-legal-entity
INSERT INTO investor_legal_entities (id, investor_id, full_name, short_name, ogrn, inn, legal_address, postal_address, email, phone) VALUES
    (302, 302,
     'Акционерное общество "ИнвестГрупп"',
     'АО "ИнвестГрупп"',
     '1037739010891',
     '7839012345',
     'г. Москва, ул. Большая Ордынка, д. 21, стр. 1',
     'г. Москва, ул. Большая Ордынка, д. 21, стр. 1',
     'investor.ul@investplatform.ru',
     '+74951234567');

SELECT setval('investor_legal_entities_id_seq', (SELECT MAX(id) FROM investor_legal_entities));

--changeset investplatform:002-mock-investor-le-executive
INSERT INTO investor_le_executives (id, investor_legal_entity_id, is_management_company, last_name, first_name, patronymic, gender, citizenship, birth_date, birth_place, id_doc_type, id_doc_series, id_doc_number, id_doc_issued_date, id_doc_issued_by, id_doc_department_code, registration_address, residential_address, inn, email, phone) VALUES
    (1, 302, FALSE,
     'Громов', 'Виктор', 'Анатольевич',
     'MALE',
     'Российская Федерация',
     '1975-01-30',
     'г. Москва',
     'Паспорт гражданина РФ',
     '4508', '112233',
     '2008-06-15',
     'Отделом УФМС России по г. Москве по Замоскворецкому р-ну',
     '770-045',
     'г. Москва, ул. Якиманка, д. 5, кв. 78',
     'г. Москва, ул. Якиманка, д. 5, кв. 78',
     '770567890123',
     'gromov@investgrupp.ru',
     '+79031234567');

SELECT setval('investor_le_executives_id_seq', (SELECT MAX(id) FROM investor_le_executives));

--changeset investplatform:002-mock-security-classifications
INSERT INTO security_classifications (id, code, name) VALUES
    (1, 'EQUITY',  'Акция'),
    (2, 'BOND',    'Облигация'),
    (3, 'CONVERT', 'Конвертируемая ценная бумага');

SELECT setval('security_classifications_id_seq', (SELECT MAX(id) FROM security_classifications));

--changeset investplatform:002-mock-security-categories
INSERT INTO security_categories (id, code, name) VALUES
    (1, 'COMMON',    'Обыкновенная'),
    (2, 'PREFERRED', 'Привилегированная'),
    (3, 'COUPON',    'Купонная');

SELECT setval('security_categories_id_seq', (SELECT MAX(id) FROM security_categories));

--changeset investplatform:002-mock-securities
INSERT INTO securities (id, securities_code, security_classification_id, security_category_id, security_type, state_reg_num, state_reg_date, isin, nominal_currency, nominal_value, quantity_in_issue, quantity_placed, form_issue, cfi_code, financial_instrument_type, emitent_id) VALUES
    (1, 'TINV-001',
     1, 1,
     'Обыкновенная акция',
     '1-01-12345-А', '2024-01-15',
     'RU000A106K11',
     'RUB', 100.0000, 100000.0000, 25000.0000,
     'именная', 'ESVUFR', 'EQUITY',
     200),
    (2, 'TINV-002',
     2, 3,
     'Облигация купонная',
     '4-01-12345-А', '2024-06-01',
     'RU000A107B22',
     'RUB', 1000.0000, 50000.0000, 10000.0000,
     'именная', 'DBFUFR', 'BOND',
     200),
    (3, 'PETR-001',
     1, 1,
     'Обыкновенная акция',
     '1-02-67890-А', '2025-03-10',
     'RU000A108C33',
     'RUB', 500.0000, 20000.0000, 5000.0000,
     'именная', 'ESVUFR', 'EQUITY',
     201);

SELECT setval('securities_id_seq', (SELECT MAX(id) FROM securities));

--changeset investplatform:002-mock-proposals
-- status_id: 1=draft, 2=pending, 3=reviewing, 4=rejected, 5=active, 6=failed, 7=completed

-- Active proposal (ООО ТехноИнвест — акции)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    issue_decision_info, placement_procedure, placement_terms, placement_conditions,
    has_preemptive_right, risk_warning,
    max_investment_amount, min_investment_amount, proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    reviewed_by, submitted_at, reviewed_at, activated_at) VALUES
    (1, 200, 5, 1, 1,
     'Размещение акций ООО "ТехноИнвест" — серия А',
     'Привлечение инвестиций для масштабирования SaaS-платформы управления данными. Средства будут направлены на расширение команды разработки и маркетинг.',
     'Неуспешное масштабирование продукта, изменение рыночной конъюнктуры, появление конкурентов с аналогичным продуктом.',
     'Риск потери ключевых сотрудников, зависимость от единственного продукта, ограниченная история операционной деятельности.',
     'Риск невозврата инвестиций, риск размытия доли при последующих раундах, отсутствие дивидендной политики на ранней стадии.',
     'Решение единственного участника №5 от 10.01.2025 о размещении дополнительных акций.',
     'Размещение осуществляется через инвестиционную платформу путём заключения договоров инвестирования.',
     'Срок размещения — 90 дней с момента публикации. Акции размещаются по номинальной стоимости 100 руб. за акцию.',
     'Размещение акций на условиях, указанных в решении о выпуске.',
     FALSE,
     'ВНИМАНИЕ: Инвестирование в ценные бумаги сопряжено с риском потери всей суммы инвестиций. Перед принятием решения внимательно ознакомьтесь с рисками.',
     10000000.00, 10000.00,
     '2025-09-01', '2025-12-01',
     'Инвестор приобретает обыкновенные именные акции по цене 100 руб. за акцию. Минимальная сумма инвестирования — 10 000 руб.',
     'Российская Федерация',
     2500000.00,
     100,
     '2025-08-20 09:00:00', '2025-08-25 14:00:00', '2025-09-01 00:00:00');

-- Draft proposal (ООО ТехноИнвест — облигации)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    risk_warning,
    max_investment_amount, min_investment_amount, proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount) VALUES
    (2, 200, 1, 1, 2,
     'Облигационный заём ООО "ТехноИнвест" — купонные облигации',
     'Привлечение заёмного финансирования для закупки серверного оборудования и развития инфраструктуры.',
     'Технологические риски, риск задержки поставок оборудования.',
     'Кредитный риск эмитента, риск невозврата основного долга.',
     'Процентный риск, риск дефолта эмитента, риск невозможности досрочного погашения.',
     'Инвестирование связано с риском потери инвестированных средств. Эмитент не гарантирует доходность вложений.',
     25000000.00, 50000.00,
     '2026-01-15', '2026-04-15',
     'Купонная облигация номиналом 1000 руб. Купонный период — 6 месяцев. Ставка купона — 14% годовых.',
     'Российская Федерация',
     0.00);

-- Active proposal (ИП Петров — акции)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    risk_warning,
    max_investment_amount, min_investment_amount, proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    reviewed_by, submitted_at, reviewed_at, activated_at) VALUES
    (3, 201, 5, 1, 3,
     'Размещение акций "ЭкоПак" (ИП Петров А.Н.)',
     'Расширение производственных мощностей по выпуску биоразлагаемой упаковки. Строительство второго цеха и закупка оборудования.',
     'Рост стоимости сырья, задержка строительства, изменение экологического законодательства.',
     'Малый масштаб бизнеса, зависимость от ключевого лица (ИП), ограниченная кредитная история.',
     'Операционные риски малого бизнеса, зависимость от ограниченного числа поставщиков.',
     'Инвестиции не застрахованы. Существует риск полной потери вложенных средств.',
     5000000.00, 5000.00,
     '2025-10-01', '2026-01-01',
     'Инвестор приобретает обыкновенные именные акции по цене 500 руб. за акцию. Минимальная сумма инвестирования — 5 000 руб.',
     'Российская Федерация',
     750000.00,
     101,
     '2025-09-10 11:00:00', '2025-09-15 16:00:00', '2025-10-01 00:00:00');

-- Rejected proposal
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    risk_warning,
    max_investment_amount, min_investment_amount, proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    reviewed_by, rejection_reason, submitted_at, reviewed_at) VALUES
    (4, 200, 4, 1,
     'Пилотный проект IoT-мониторинга',
     'Запуск IoT-платформы мониторинга промышленного оборудования.',
     'Высокая конкуренция, незрелость технологии.',
     'Отсутствие подтверждённого спроса на продукт.',
     'Ранняя стадия проекта, высокая степень неопределённости.',
     'Крайне высокий уровень риска. Проект находится на стадии идеи.',
     3000000.00, 10000.00,
     '2025-06-01', '2025-09-01',
     'Договор займа с конвертацией в долю.',
     'Российская Федерация',
     0.00,
     100,
     'Недостаточно проработан бизнес-план. Отсутствуют финансовые прогнозы и анализ рынка. Необходимо доработать документацию и предоставить MVP продукта.',
     '2025-05-01 10:00:00', '2025-05-10 15:00:00');

SELECT setval('investment_proposals_id_seq', (SELECT MAX(id) FROM investment_proposals));

--changeset investplatform:002-mock-proposal-status-history
INSERT INTO proposal_status_history (proposal_id, old_status_id, new_status_id, changed_by, comment) VALUES
    -- Proposal 1: draft -> pending -> reviewing -> active
    (1, NULL, 1, 200, 'ИП создано'),
    (1, 1, 2, 200, 'Отправлено на рассмотрение'),
    (1, 2, 3, 100, 'Принято к рассмотрению'),
    (1, 3, 5, 100, 'Проверка пройдена, ИП активировано'),
    -- Proposal 3: draft -> pending -> reviewing -> active
    (3, NULL, 1, 201, 'ИП создано'),
    (3, 1, 2, 201, 'Отправлено на рассмотрение'),
    (3, 2, 3, 101, 'Принято к рассмотрению'),
    (3, 3, 5, 101, 'Одобрено, опубликовано'),
    -- Proposal 4: draft -> pending -> reviewing -> rejected
    (4, NULL, 1, 200, 'ИП создано'),
    (4, 1, 2, 200, 'Отправлено на рассмотрение'),
    (4, 2, 3, 100, 'Принято к рассмотрению'),
    (4, 3, 4, 100, 'Отклонено. Недостаточно проработана документация.');

--changeset investplatform:002-mock-payments
INSERT INTO payments (id, yukassa_payment_id, personal_account_id, payment_type, direction, amount, currency, yukassa_status, payment_method_type, description, idempotency_key, paid_at) VALUES
    -- Investor 300 (Сидорова) deposits
    (1, 'pay_2a1b3c4d-0001', 300, 'DEPOSIT', 'INBOUND', 300000.00, 'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-001', '2025-08-01 10:15:00'),
    (2, 'pay_2a1b3c4d-0002', 300, 'DEPOSIT', 'INBOUND', 250000.00, 'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-002', '2025-09-10 12:00:00'),
    -- Investor 301 (Волков) deposit
    (3, 'pay_2a1b3c4d-0003', 301, 'DEPOSIT', 'INBOUND', 250000.00, 'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-003', '2025-07-20 09:30:00'),
    -- Investor 302 (АО ИнвестГрупп) deposits
    (4, 'pay_2a1b3c4d-0004', 302, 'DEPOSIT', 'INBOUND', 2000000.00, 'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-004', '2025-06-01 08:00:00'),
    (5, 'pay_2a1b3c4d-0005', 302, 'DEPOSIT', 'INBOUND', 1200000.00, 'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-005', '2025-09-05 14:20:00'),
    -- Emitent 200 deposit (from completed proposals)
    (6, 'pay_2a1b3c4d-0006', 200, 'TRANSFER', 'INBOUND', 1500000.00, 'RUB', 'succeeded', 'internal', 'Перевод средств по ИП №1', 'idem-pay-006', '2025-10-01 12:00:00');

SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));

--changeset investplatform:002-mock-account-transactions
INSERT INTO account_transactions (personal_account_id, transaction_type, amount, balance_after, payment_id, description) VALUES
    (300, 'DEPOSIT',  300000.00, 300000.00, 1, 'Пополнение счёта'),
    (300, 'DEPOSIT',  250000.00, 550000.00, 2, 'Пополнение счёта'),
    (300, 'HOLD',      50000.00, 550000.00, NULL, 'Резервирование средств по ДИ'),
    (301, 'DEPOSIT',  250000.00, 250000.00, 3, 'Пополнение счёта'),
    (302, 'DEPOSIT', 2000000.00, 2000000.00, 4, 'Пополнение счёта'),
    (302, 'DEPOSIT', 1200000.00, 3200000.00, 5, 'Пополнение счёта'),
    (302, 'HOLD',     200000.00, 3200000.00, NULL, 'Резервирование средств по ДИ'),
    (200, 'DEPOSIT', 1500000.00, 1500000.00, 6, 'Перевод средств по ИП №1');

--changeset investplatform:002-mock-contracts
-- contract_status_id: 1=reviewing, 2=withdrawn, 3=rejected, 4=approved, 5=completed, 6=failed

-- Active contracts for proposal 1 (ТехноИнвест акции)
INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, payment_id, signed_at, reviewed_at, created_at) VALUES
    (1, 'DI-2025-000001', 1, 300, 4, 50000.00, 1, 500.0000, 100.0000, 100, 2, '2025-09-15 10:00:00', '2025-09-16 11:00:00', '2025-09-15 10:00:00'),
    (2, 'DI-2025-000002', 1, 302, 4, 200000.00, 1, 2000.0000, 100.0000, 100, 5, '2025-09-20 15:00:00', '2025-09-21 10:00:00', '2025-09-20 15:00:00');

-- Contract for proposal 3 (ЭкоПак)
INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, payment_id, signed_at, reviewed_at, created_at) VALUES
    (3, 'DI-2025-000003', 3, 300, 1, 50000.00, 3, 100.0000, 500.0000, NULL, NULL, '2025-10-05 09:00:00', NULL, '2025-10-05 09:00:00');

SELECT setval('investment_contracts_id_seq', (SELECT MAX(id) FROM investment_contracts));

--changeset investplatform:002-mock-contract-status-history
INSERT INTO contract_status_history (contract_id, old_status_id, new_status_id, changed_by, comment) VALUES
    (1, NULL, 1, 300, 'ДИ создан'),
    (1, 1, 4, 100, 'ДИ одобрен оператором'),
    (2, NULL, 1, 302, 'ДИ создан'),
    (2, 1, 4, 100, 'ДИ одобрен оператором'),
    (3, NULL, 1, 300, 'ДИ создан');

--changeset investplatform:002-mock-security-balances
INSERT INTO security_balances (id, personal_account_id, security_id, quantity, quantity_blocked, quantity_pledged, quantity_encumbered) VALUES
    (1, 300, 1, 500.0000, 0.0000, 0.0000, 0.0000),
    (2, 302, 1, 2000.0000, 0.0000, 0.0000, 0.0000),
    (3, 200, 1, 72500.0000, 0.0000, 0.0000, 0.0000),
    (4, 200, 2, 40000.0000, 0.0000, 0.0000, 0.0000),
    (5, 201, 3, 15000.0000, 0.0000, 0.0000, 0.0000);

SELECT setval('security_balances_id_seq', (SELECT MAX(id) FROM security_balances));

--changeset investplatform:002-mock-emitent-documents
INSERT INTO emitent_documents (id, emitent_id, document_type_id, report_year, file_name, file_path, file_size, mime_type) VALUES
    (1, 200, 1, 2024, 'technoinvest_fin_report_2024.pdf',  'emitents/200/financial_report_2024.pdf',  2048576, 'application/pdf'),
    (2, 200, 2, 2024, 'technoinvest_audit_2024.pdf',       'emitents/200/audit_conclusion_2024.pdf',  1536000, 'application/pdf'),
    (3, 200, 3, 2024, 'technoinvest_charter.pdf',          'emitents/200/charter.pdf',                 512000, 'application/pdf'),
    (4, 200, 4, 2024, 'technoinvest_egrul.pdf',            'emitents/200/egrul_extract_2024.pdf',      256000, 'application/pdf'),
    (5, 201, 1, 2024, 'ecopak_fin_report_2024.pdf',        'emitents/201/financial_report_2024.pdf',   768000, 'application/pdf'),
    (6, 201, 4, 2024, 'ecopak_egrip.pdf',                  'emitents/201/egrip_extract_2024.pdf',      128000, 'application/pdf');

SELECT setval('emitent_documents_id_seq', (SELECT MAX(id) FROM emitent_documents));

--changeset investplatform:002-mock-investor-documents
INSERT INTO investor_documents (id, investor_id, document_type_id, report_year, file_name, file_path, file_size, mime_type) VALUES
    -- Сидорова (физлицо)
    (1, 300, 1, 2025, 'sidorova_passport_main.pdf',   'investors/300/passport_main.pdf',         1024000, 'application/pdf'),
    (2, 300, 2, 2025, 'sidorova_passport_reg.pdf',    'investors/300/passport_registration.pdf',  512000, 'application/pdf'),
    (3, 300, 4, 2025, 'sidorova_snils.pdf',           'investors/300/snils.pdf',                  256000, 'application/pdf'),
    -- Волков (ИП)
    (4, 301, 1, 2025, 'volkov_passport_main.pdf',     'investors/301/passport_main.pdf',         1024000, 'application/pdf'),
    (5, 301, 5, 2025, 'volkov_ip_registration.pdf',   'investors/301/ip_registration.pdf',        384000, 'application/pdf'),
    -- АО ИнвестГрупп (ЮЛ)
    (6, 302, 6, 2025, 'investgrupp_egrul.pdf',        'investors/302/le_registration.pdf',        640000, 'application/pdf'),
    (7, 302, 8, 2025, 'investgrupp_charter.pdf',      'investors/302/charter.pdf',                768000, 'application/pdf'),
    (8, 302, 9, 2025, 'investgrupp_eio_decision.pdf', 'investors/302/executive_decision.pdf',     256000, 'application/pdf');

SELECT setval('investor_documents_id_seq', (SELECT MAX(id) FROM investor_documents));

--changeset investplatform:002-mock-proposal-documents
INSERT INTO proposal_documents (id, proposal_id, document_type_id, file_name, file_path, file_size, mime_type) VALUES
    -- Proposal 1 (ТехноИнвест акции)
    (1, 1, 1, 'technoinvest_fin_2024.pdf',        'proposals/1/financial_report.pdf',       2048576, 'application/pdf'),
    (2, 1, 3, 'technoinvest_issue_decision.pdf',   'proposals/1/issue_decision.pdf',         512000, 'application/pdf'),
    (3, 1, 6, 'technoinvest_draft_contract.pdf',   'proposals/1/draft_contract.pdf',        1024000, 'application/pdf'),
    (4, 1, 7, 'technoinvest_risk_warning.pdf',     'proposals/1/risk_warning.pdf',           384000, 'application/pdf'),
    -- Proposal 3 (ЭкоПак)
    (5, 3, 1, 'ecopak_fin_2024.pdf',              'proposals/3/financial_report.pdf',        768000, 'application/pdf'),
    (6, 3, 6, 'ecopak_draft_contract.pdf',         'proposals/3/draft_contract.pdf',         640000, 'application/pdf'),
    (7, 3, 7, 'ecopak_risk_warning.pdf',           'proposals/3/risk_warning.pdf',           256000, 'application/pdf');

SELECT setval('proposal_documents_id_seq', (SELECT MAX(id) FROM proposal_documents));

--changeset investplatform:002-mock-pd-consent-versions
INSERT INTO pd_consent_versions (id, consent_type_id, version_number, content, effective_from) VALUES
    (1, 1, 1, 'Я даю согласие на обработку моих персональных данных для создания учётной записи, идентификации и аутентификации на инвестиционной платформе в соответствии с ФЗ-152 «О персональных данных».', '2025-01-01'),
    (2, 2, 1, 'Я даю согласие на обработку моих персональных данных в рамках инвестиционной деятельности, включая заключение и исполнение договоров инвестирования, ведение реестра владельцев ценных бумаг.', '2025-01-01'),
    (3, 3, 1, 'Я даю согласие на передачу моих персональных данных в Банк России и иные уполномоченные органы в соответствии с ФЗ-259 «О привлечении инвестиций с использованием инвестиционных платформ».', '2025-01-01'),
    (4, 4, 1, 'Я даю согласие на получение информационных и рекламных материалов по электронной почте и SMS.', '2025-01-01'),
    (5, 5, 1, 'Я даю согласие на трансграничную передачу моих персональных данных в юрисдикции, обеспечивающие адекватную защиту прав субъектов персональных данных.', '2025-01-01');

SELECT setval('pd_consent_versions_id_seq', (SELECT MAX(id) FROM pd_consent_versions));

--changeset investplatform:002-mock-pd-consents
INSERT INTO pd_consents (user_id, consent_version_id, status, accepted_at, ip_address, user_agent) VALUES
    -- Investor 300 (Сидорова) — все обязательные + маркетинг
    (300, 1, 'ACTIVE', '2025-06-01 10:00:00', '192.168.1.100', 'Mozilla/5.0 Chrome/120'),
    (300, 2, 'ACTIVE', '2025-06-01 10:00:30', '192.168.1.100', 'Mozilla/5.0 Chrome/120'),
    (300, 3, 'ACTIVE', '2025-06-01 10:01:00', '192.168.1.100', 'Mozilla/5.0 Chrome/120'),
    (300, 4, 'ACTIVE', '2025-06-01 10:01:30', '192.168.1.100', 'Mozilla/5.0 Chrome/120'),
    -- Investor 301 (Волков) — только обязательные
    (301, 1, 'ACTIVE', '2025-07-15 14:30:00', '10.0.0.50', 'Mozilla/5.0 Firefox/119'),
    (301, 2, 'ACTIVE', '2025-07-15 14:30:30', '10.0.0.50', 'Mozilla/5.0 Firefox/119'),
    (301, 3, 'ACTIVE', '2025-07-15 14:31:00', '10.0.0.50', 'Mozilla/5.0 Firefox/119'),
    -- Investor 302 (АО ИнвестГрупп)
    (302, 1, 'ACTIVE', '2025-05-20 09:00:00', '172.16.0.10', 'Mozilla/5.0 Edge/120'),
    (302, 2, 'ACTIVE', '2025-05-20 09:00:30', '172.16.0.10', 'Mozilla/5.0 Edge/120'),
    (302, 3, 'ACTIVE', '2025-05-20 09:01:00', '172.16.0.10', 'Mozilla/5.0 Edge/120'),
    -- Emitent 200 (ТехноИнвест)
    (200, 1, 'ACTIVE', '2025-04-01 08:00:00', '192.168.0.10', 'Mozilla/5.0 Chrome/120'),
    (200, 2, 'ACTIVE', '2025-04-01 08:00:30', '192.168.0.10', 'Mozilla/5.0 Chrome/120'),
    (200, 3, 'ACTIVE', '2025-04-01 08:01:00', '192.168.0.10', 'Mozilla/5.0 Chrome/120'),
    -- Emitent 201 (Петров)
    (201, 1, 'ACTIVE', '2025-04-15 11:00:00', '192.168.0.20', 'Mozilla/5.0 Safari/17'),
    (201, 2, 'ACTIVE', '2025-04-15 11:00:30', '192.168.0.20', 'Mozilla/5.0 Safari/17'),
    (201, 3, 'ACTIVE', '2025-04-15 11:01:00', '192.168.0.20', 'Mozilla/5.0 Safari/17');

--changeset investplatform:002-mock-registry-operations
-- Registry operations for placed securities
INSERT INTO registry_operations (id, operation_type_id, operation_name, operation_kind, processing_datetime, processing_reference, date_state, account_transfer_id, account_receive_id, security_id, quantity, settlement_currency, settlement_amount, content) VALUES
    -- Placement of TINV-001 shares
    (1, 8, 'Размещение акций TINV-001 (ООО ТехноИнвест)', 'EMISSION',
     '2025-01-20 10:00:00', 'REG-2025-0001', '2025-01-20',
     NULL, 200, 1, 25000.0000, 'RUB', 2500000.00,
     'Первичное размещение обыкновенных именных акций по решению о выпуске.'),
    -- Transfer to investor 300 (Сидорова)
    (2, 1, 'Переход прав на акции TINV-001 к инвестору Сидорова Е.А.', 'TRANSACTION',
     '2025-09-16 12:00:00', 'REG-2025-0002', '2025-09-16',
     200, 300, 1, 500.0000, 'RUB', 50000.00,
     'Перевод акций по договору инвестирования DI-2025-000001.'),
    -- Transfer to investor 302 (АО ИнвестГрупп)
    (3, 1, 'Переход прав на акции TINV-001 к АО "ИнвестГрупп"', 'TRANSACTION',
     '2025-09-21 11:00:00', 'REG-2025-0003', '2025-09-21',
     200, 302, 1, 2000.0000, 'RUB', 200000.00,
     'Перевод акций по договору инвестирования DI-2025-000002.'),
    -- Placement of TINV-002 bonds
    (4, 8, 'Размещение облигаций TINV-002 (ООО ТехноИнвест)', 'EMISSION',
     '2025-06-10 10:00:00', 'REG-2025-0004', '2025-06-10',
     NULL, 200, 2, 10000.0000, 'RUB', 10000000.00,
     'Размещение купонных облигаций.'),
    -- Placement of PETR-001 shares
    (5, 8, 'Размещение акций PETR-001 (ИП Петров А.Н.)', 'EMISSION',
     '2025-03-15 10:00:00', 'REG-2025-0005', '2025-03-15',
     NULL, 201, 3, 5000.0000, 'RUB', 2500000.00,
     'Первичное размещение обыкновенных именных акций.');

SELECT setval('registry_operations_id_seq', (SELECT MAX(id) FROM registry_operations));

--changeset investplatform:002-mock-registry-operation-documents
INSERT INTO registry_operation_documents (id, registry_operation_id, in_doc_num, in_reg_date, out_doc_num, out_doc_date) VALUES
    (1, 1, 'ВХ-2025-001', '2025-01-19 09:00:00', 'ИСХ-2025-001', '2025-01-20 10:00:00'),
    (2, 2, 'ВХ-2025-002', '2025-09-15 10:00:00', 'ИСХ-2025-002', '2025-09-16 12:00:00'),
    (3, 3, 'ВХ-2025-003', '2025-09-20 15:00:00', 'ИСХ-2025-003', '2025-09-21 11:00:00'),
    (4, 4, 'ВХ-2025-004', '2025-06-09 09:00:00', 'ИСХ-2025-004', '2025-06-10 10:00:00'),
    (5, 5, 'ВХ-2025-005', '2025-03-14 09:00:00', 'ИСХ-2025-005', '2025-03-15 10:00:00');

SELECT setval('registry_operation_documents_id_seq', (SELECT MAX(id) FROM registry_operation_documents));

--changeset investplatform:002-mock-notifications
INSERT INTO notifications (user_id, event_type_id, channel_id, entity_type, entity_id, title, body, delivery_status, is_read, read_at, sent_at, delivered_at) VALUES
    -- Emitent 200: proposal approved
    (200, 2, 1, 'investment_proposal', 1, 'ИП одобрено', 'Ваше инвестиционное предложение "Размещение акций ООО ТехноИнвест — серия А" одобрено оператором и будет опубликовано.', 'DELIVERED', TRUE, '2025-08-25 15:00:00', '2025-08-25 14:00:00', '2025-08-25 14:00:05'),
    -- Emitent 200: proposal rejected
    (200, 3, 1, 'investment_proposal', 4, 'ИП отклонено', 'Ваше инвестиционное предложение "Пилотный проект IoT-мониторинга" отклонено. Причина: недостаточно проработан бизнес-план.', 'DELIVERED', TRUE, '2025-05-10 16:00:00', '2025-05-10 15:00:00', '2025-05-10 15:00:05'),
    -- Investor 300: contract approved
    (300, 8, 1, 'investment_contract', 1, 'ДИ одобрен', 'Ваш договор инвестирования DI-2025-000001 одобрен оператором.', 'DELIVERED', TRUE, '2025-09-16 12:00:00', '2025-09-16 11:00:00', '2025-09-16 11:00:05'),
    -- Investor 300: payment received
    (300, 13, 1, 'payment', 1, 'Платёж получен', 'На ваш счёт зачислено 300 000,00 руб.', 'DELIVERED', TRUE, '2025-08-01 11:00:00', '2025-08-01 10:15:00', '2025-08-01 10:15:05'),
    -- Investor 302: contract approved
    (302, 8, 1, 'investment_contract', 2, 'ДИ одобрен', 'Ваш договор инвестирования DI-2025-000002 одобрен оператором.', 'DELIVERED', FALSE, NULL, '2025-09-21 10:00:00', '2025-09-21 10:00:05'),
    -- Emitent 201: proposal activated
    (201, 4, 1, 'investment_proposal', 3, 'ИП опубликовано', 'Ваше инвестиционное предложение "Размещение акций ЭкоПак" опубликовано и доступно инвесторам.', 'DELIVERED', TRUE, '2025-10-01 01:00:00', '2025-10-01 00:00:00', '2025-10-01 00:00:05');

--changeset investplatform:002-mock-auth-audit-log
INSERT INTO auth_audit_log (user_id, event_type, ip_address, user_agent, is_successful) VALUES
    (100, 'LOGIN',  '192.168.1.1',   'Mozilla/5.0 Chrome/120', TRUE),
    (101, 'LOGIN',  '192.168.1.2',   'Mozilla/5.0 Chrome/120', TRUE),
    (200, 'LOGIN',  '192.168.0.10',  'Mozilla/5.0 Chrome/120', TRUE),
    (201, 'LOGIN',  '192.168.0.20',  'Mozilla/5.0 Safari/17',  TRUE),
    (300, 'LOGIN',  '192.168.1.100', 'Mozilla/5.0 Chrome/120', TRUE),
    (300, 'LOGIN',  '10.0.0.99',     'Mozilla/5.0 Chrome/120', FALSE),
    (301, 'LOGIN',  '10.0.0.50',     'Mozilla/5.0 Firefox/119', TRUE),
    (302, 'LOGIN',  '172.16.0.10',   'Mozilla/5.0 Edge/120',   TRUE);
