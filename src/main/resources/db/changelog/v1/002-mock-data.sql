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
    (101, 'operator2@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 2, TRUE, TRUE, FALSE),
    (102, 'operator3@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 2, TRUE, TRUE, FALSE),
    (103, 'operator4@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 2, TRUE, TRUE, FALSE),
    (104, 'operator5@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 2, TRUE, TRUE, FALSE);

-- Emitents
INSERT INTO users (id, email, password_hash, role_id, is_enabled, is_account_non_locked, is_2fa_enabled) VALUES
    (200, 'emitent.ooo@investplatform.ru',   '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 3, TRUE, TRUE, FALSE),
    (201, 'emitent.ip@investplatform.ru',    '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 3, TRUE, TRUE, FALSE),
    (202, 'emitent.stroi@investplatform.ru', '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 3, TRUE, TRUE, FALSE),
    (203, 'emitent.agro@investplatform.ru',  '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 3, TRUE, TRUE, FALSE),
    (204, 'emitent.kuzn@investplatform.ru',  '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 3, TRUE, TRUE, FALSE),
    (205, 'emitent.med@investplatform.ru',   '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 3, TRUE, TRUE, FALSE);

-- Investors
INSERT INTO users (id, email, password_hash, role_id, is_enabled, is_account_non_locked, is_2fa_enabled) VALUES
    (300, 'investor.fl@investplatform.ru',      '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (301, 'investor.ip@investplatform.ru',      '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (302, 'investor.ul@investplatform.ru',      '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (303, 'morozov.a@investplatform.ru',        '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (304, 'nikolaeva.d@investplatform.ru',      '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (305, 'pavlov.k@investplatform.ru',         '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (306, 'kapitalinvest@investplatform.ru',    '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (307, 'belov.m@investplatform.ru',          '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (308, 'tikhonova.a@investplatform.ru',      '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE),
    (309, 'venchurfond@investplatform.ru',      '$2b$10$2GQ.ifnsR27fTd4SVmxwrOjjiyY4./OkboVDuGe9UpJlpCDt6WIfG', 4, TRUE, TRUE, FALSE);

-- Reset sequence
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

--changeset investplatform:002-mock-operators
INSERT INTO operators (id, user_id, last_name, first_name, patronymic) VALUES
    (100, 100, 'Смирнова',  'Анна',     'Владимировна'),
    (101, 101, 'Козлов',    'Дмитрий',  'Сергеевич'),
    (102, 102, 'Белова',    'Ольга',    'Игоревна'),
    (103, 103, 'Федоров',   'Артём',    'Павлович'),
    (104, 104, 'Морозова',  'Светлана', 'Андреевна');

SELECT setval('operators_id_seq', (SELECT MAX(id) FROM operators));

--changeset investplatform:002-mock-personal-accounts
-- Emitent accounts (account_type 01 = Счёт владельца)
INSERT INTO personal_accounts (id, account_number, account_type_id, balance, hold_amount) VALUES
    (200, 'EM-2025-000001', 1, 1500000.00,  0.00),
    (201, 'EM-2025-000002', 1,  350000.00,  0.00),
    (202, 'EM-2025-000003', 1, 2000000.00,  0.00),
    (203, 'EM-2025-000004', 1,  800000.00,  0.00),
    (204, 'EM-2025-000005', 1,  150000.00,  0.00),
    (205, 'EM-2025-000006', 1, 3000000.00,  0.00);

-- Investor accounts
INSERT INTO personal_accounts (id, account_number, account_type_id, balance, hold_amount) VALUES
    (300, 'IN-2025-000001', 1,  500000.00,   50000.00),
    (301, 'IN-2025-000002', 1,  250000.00,       0.00),
    (302, 'IN-2025-000003', 1, 3000000.00,  200000.00),
    (303, 'IN-2025-000004', 1,  800000.00,  200000.00),
    (304, 'IN-2025-000005', 1, 1000000.00,  125000.00),
    (305, 'IN-2025-000006', 1,  200000.00,       0.00),
    (306, 'IN-2025-000007', 1, 5000000.00,  430000.00),
    (307, 'IN-2025-000008', 1,  150000.00,       0.00),
    (308, 'IN-2025-000009', 1,  100000.00,       0.00),
    (309, 'IN-2025-000010', 1, 5000000.00,  400000.00);

SELECT setval('personal_accounts_id_seq', (SELECT MAX(id) FROM personal_accounts));

--changeset investplatform:002-mock-emitents
INSERT INTO emitents (id, user_id, emitent_type, personal_account_id) VALUES
    (200, 200, 'LEGAL_ENTITY',         200),
    (201, 201, 'PRIVATE_ENTREPRENEUR', 201),
    (202, 202, 'LEGAL_ENTITY',         202),
    (203, 203, 'LEGAL_ENTITY',         203),
    (204, 204, 'PRIVATE_ENTREPRENEUR', 204),
    (205, 205, 'LEGAL_ENTITY',         205);

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
     5000000.00),
    (202, 202,
     'Акционерное общество "СтройИнновация"',
     'АО "СтройИнновация"',
     '1037739085636',
     '7710140679',
     '771001001',
     'г. Москва, Пресненская наб., д. 8, стр. 1, оф. 1205',
     'г. Москва, Пресненская наб., д. 8, стр. 1, оф. 1205',
     '29403850',
     '45286552000',
     'АО',
     'Строительная компания полного цикла: от проектирования до сдачи объекта. Портфель текущих проектов — 12 жилых комплексов в Москве и МО. Выручка за 2025 — 1,2 млрд руб.',
     25000000.00),
    (203, 203,
     'Общество с ограниченной ответственностью "АгроТех"',
     'ООО "АгроТех"',
     '1145476123456',
     '5406789012',
     '540601001',
     'г. Новосибирск, ул. Кирова, д. 86, оф. 410',
     'г. Новосибирск, ул. Кирова, д. 86, оф. 410',
     '45329870',
     '50401000000',
     'ООО',
     'Агротехнологический стартап, разрабатывающий системы точного земледелия на основе IoT-датчиков и AI-аналитики. Пилот развёрнут на 5 фермерских хозяйствах в Новосибирской области. Выручка за 2025 — 32 млн руб.',
     3000000.00),
    (205, 205,
     'Общество с ограниченной ответственностью "МедТехника"',
     'ООО "МедТехника"',
     '1167746543210',
     '7728456789',
     '772801001',
     'г. Москва, ул. Профсоюзная, д. 65, корп. 2, оф. 501',
     'г. Москва, ул. Профсоюзная, д. 65, корп. 2, оф. 501',
     '51284630',
     '45293596000',
     'ООО',
     'Производитель портативных медицинских диагностических приборов. Имеет 3 патента, регистрационные удостоверения Росздравнадзора на 4 изделия. Выручка за 2025 — 180 млн руб. Чистая прибыль — 28 млн руб.',
     15000000.00);

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
     2000000.00),
    (204, 204,
     'Кузнецова', 'Мария', 'Викторовна',
     '1991-07-22',
     'г. Казань',
     '320169000054321',
     '165098765432',
     'г. Казань, ул. Баумана, д. 33, кв. 8',
     '98765432109',
     'ИП Кузнецова М.В. — сеть кофеен "КофеЛаб" (12 точек в Казани и Нижнем Новгороде). Годовой оборот — 45 млн руб. Чистая прибыль — 7 млн руб. Планируется расширение в Самару и Уфу.',
     4000000.00);

SELECT setval('emitent_private_entrepreneurs_id_seq', (SELECT MAX(id) FROM emitent_private_entrepreneurs));

--changeset investplatform:002-mock-emitent-le-shareholders
INSERT INTO emitent_le_shareholders (id, emitent_legal_entity_id, person_type, full_name, country, inn, ogrn, vote_share_percent, ownership_basis) VALUES
    -- ООО "ТехноИнвест" (entity 200)
    (1, 200, 'INDIVIDUAL',   'Иванов Сергей Петрович',     'Россия', '770812345678', NULL,            60.00, 'Учредительный договор от 01.01.2020'),
    (2, 200, 'LEGAL_ENTITY', 'ООО "Стартап Венчурс"',      'Россия', '7709456123',   '1177746123456', 40.00, 'Договор купли-продажи доли от 15.06.2022'),
    -- АО "СтройИнновация" (entity 202)
    (3, 202, 'INDIVIDUAL',   'Краснов Андрей Михайлович',   'Россия', '771034567890', NULL,            45.00, 'Учредительный договор от 12.03.2018'),
    (4, 202, 'LEGAL_ENTITY', 'ЗАО "ДевелопГрупп"',         'Россия', '7714321098',   '1087746098765', 35.00, 'Договор купли-продажи акций от 01.09.2020'),
    (5, 202, 'INDIVIDUAL',   'Ефимова Татьяна Владимировна','Россия', '771256789012', NULL,            20.00, 'Наследство, свидетельство от 15.02.2023'),
    -- ООО "АгроТех" (entity 203)
    (6, 203, 'INDIVIDUAL',   'Романов Дмитрий Евгеньевич',  'Россия', '540698765432', NULL,            70.00, 'Учредительный договор от 20.05.2021'),
    (7, 203, 'INDIVIDUAL',   'Жукова Наталья Сергеевна',    'Россия', '540687654321', NULL,            30.00, 'Учредительный договор от 20.05.2021'),
    -- ООО "МедТехника" (entity 205)
    (8, 205, 'LEGAL_ENTITY', 'ООО "ФармГрупп"',             'Россия', '7735678901',   '1157746234567', 51.00, 'Договор об учреждении от 10.01.2019'),
    (9, 205, 'INDIVIDUAL',   'Ковалёв Илья Борисович',      'Россия', '772890123456', NULL,            49.00, 'Договор об учреждении от 10.01.2019');

SELECT setval('emitent_le_shareholders_id_seq', (SELECT MAX(id) FROM emitent_le_shareholders));

--changeset investplatform:002-mock-emitent-le-governing
INSERT INTO emitent_le_governing_bodies (id, emitent_legal_entity_id, body_name) VALUES
    -- ООО "ТехноИнвест" (entity 200)
    (1, 200, 'Общее собрание участников'),
    (2, 200, 'Генеральный директор'),
    -- АО "СтройИнновация" (entity 202)
    (3, 202, 'Совет директоров'),
    (4, 202, 'Генеральный директор'),
    -- ООО "АгроТех" (entity 203)
    (5, 203, 'Общее собрание участников'),
    (6, 203, 'Директор'),
    -- ООО "МедТехника" (entity 205)
    (7, 205, 'Общее собрание участников'),
    (8, 205, 'Генеральный директор');

INSERT INTO emitent_le_governing_members (id, governing_body_id, person_type, full_name, country, inn) VALUES
    -- ТехноИнвест
    (1, 1, 'INDIVIDUAL', 'Иванов Сергей Петрович',       'Россия', '770812345678'),
    (2, 2, 'INDIVIDUAL', 'Иванов Сергей Петрович',       'Россия', '770812345678'),
    -- СтройИнновация
    (3, 3, 'INDIVIDUAL', 'Краснов Андрей Михайлович',     'Россия', '771034567890'),
    (4, 3, 'INDIVIDUAL', 'Ефимова Татьяна Владимировна',  'Россия', '771256789012'),
    (5, 4, 'INDIVIDUAL', 'Краснов Андрей Михайлович',     'Россия', '771034567890'),
    -- АгроТех
    (6, 5, 'INDIVIDUAL', 'Романов Дмитрий Евгеньевич',    'Россия', '540698765432'),
    (7, 5, 'INDIVIDUAL', 'Жукова Наталья Сергеевна',      'Россия', '540687654321'),
    (8, 6, 'INDIVIDUAL', 'Романов Дмитрий Евгеньевич',    'Россия', '540698765432'),
    -- МедТехника
    (9,  7, 'INDIVIDUAL', 'Ковалёв Илья Борисович',       'Россия', '772890123456'),
    (10, 7, 'LEGAL_ENTITY','ООО "ФармГрупп"',             'Россия', '7735678901'),
    (11, 8, 'INDIVIDUAL', 'Ковалёв Илья Борисович',       'Россия', '772890123456');

SELECT setval('emitent_le_governing_bodies_id_seq', (SELECT MAX(id) FROM emitent_le_governing_bodies));
SELECT setval('emitent_le_governing_members_id_seq', (SELECT MAX(id) FROM emitent_le_governing_members));

--changeset investplatform:002-mock-emitent-le-minority
INSERT INTO emitent_le_minority_info (id, emitent_legal_entity_id, total_shares_count, total_share_percent) VALUES
    (1, 200, 0, 0.00),
    (2, 202, 0, 0.00),
    (3, 203, 0, 0.00),
    (4, 205, 0, 0.00);

SELECT setval('emitent_le_minority_info_id_seq', (SELECT MAX(id) FROM emitent_le_minority_info));

--changeset investplatform:002-mock-okveds
INSERT INTO okveds (id, code, name) VALUES
    (1,  '62.01', 'Разработка компьютерного программного обеспечения'),
    (2,  '62.02', 'Деятельность консультативная и работы в области компьютерных технологий'),
    (3,  '22.22', 'Производство пластмассовых изделий для упаковывания товаров'),
    (4,  '46.76', 'Торговля оптовая прочими промежуточными продуктами'),
    (5,  '41.20', 'Строительство жилых и нежилых зданий'),
    (6,  '71.12', 'Деятельность в области инженерных изысканий'),
    (7,  '01.11', 'Выращивание зерновых культур'),
    (8,  '01.13', 'Выращивание овощей, бахчевых, корнеплодных и клубнеплодных культур'),
    (9,  '26.60', 'Производство облучающего и электротерапевтического оборудования, применяемого в медицинских целях'),
    (10, '46.46', 'Торговля оптовая фармацевтическими изделиями'),
    (11, '56.10', 'Деятельность ресторанов и услуги по доставке продуктов питания'),
    (12, '47.29', 'Торговля розничная прочими пищевыми продуктами в специализированных магазинах');

SELECT setval('okveds_id_seq', (SELECT MAX(id) FROM okveds));

--changeset investplatform:002-mock-emitent-okved
INSERT INTO emitent_okved (emitent_id, okved_id, is_primary) VALUES
    -- ТехноИнвест (200)
    (200, 1, TRUE),
    (200, 2, FALSE),
    -- ИП Петров (201)
    (201, 3, TRUE),
    (201, 4, FALSE),
    -- СтройИнновация (202)
    (202, 5, TRUE),
    (202, 6, FALSE),
    -- АгроТех (203)
    (203, 7, TRUE),
    (203, 8, FALSE),
    -- ИП Кузнецова (204)
    (204, 11, TRUE),
    (204, 12, FALSE),
    -- МедТехника (205)
    (205, 9, TRUE),
    (205, 10, FALSE);

--changeset investplatform:002-mock-investors
INSERT INTO investors (id, user_id, investor_type, personal_account_id, is_qualified, qualified_at, qualified_basis, risk_declaration_accepted, risk_accepted_at) VALUES
    (300, 300, 'INDIVIDUAL',            300, FALSE, NULL,                       NULL,                                                            TRUE,  '2025-12-01 10:00:00'),
    (301, 301, 'PRIVATE_ENTREPRENEUR',  301, FALSE, NULL,                       NULL,                                                            TRUE,  '2026-01-15 14:30:00'),
    (302, 302, 'LEGAL_ENTITY',          302, TRUE,  '2025-11-20 09:00:00',      'Квалифицированный инвестор по п.2 ст.51.2 ФЗ-39',              TRUE,  '2025-11-20 09:00:00'),
    (303, 303, 'INDIVIDUAL',            303, FALSE, NULL,                       NULL,                                                            TRUE,  '2026-01-10 12:00:00'),
    (304, 304, 'INDIVIDUAL',            304, TRUE,  '2025-12-05 11:00:00',      'Квалифицированный инвестор: размер активов более 6 млн руб.',   TRUE,  '2025-12-05 11:00:00'),
    (305, 305, 'PRIVATE_ENTREPRENEUR',  305, FALSE, NULL,                       NULL,                                                            TRUE,  '2026-02-01 09:00:00'),
    (306, 306, 'LEGAL_ENTITY',          306, TRUE,  '2025-10-15 10:00:00',      'Квалифицированный инвестор: юридическое лицо с активами > 200 млн руб.', TRUE, '2025-10-15 10:00:00'),
    (307, 307, 'INDIVIDUAL',            307, FALSE, NULL,                       NULL,                                                            TRUE,  '2026-02-20 16:00:00'),
    (308, 308, 'INDIVIDUAL',            308, FALSE, NULL,                       NULL,                                                            FALSE, NULL),
    (309, 309, 'LEGAL_ENTITY',          309, TRUE,  '2025-09-01 08:00:00',      'Квалифицированный инвестор: лицензированный участник рынка ценных бумаг', TRUE, '2025-09-01 08:00:00');

SELECT setval('investors_id_seq', (SELECT MAX(id) FROM investors));

--changeset investplatform:002-mock-investor-individual
INSERT INTO investor_individuals (id, investor_id, last_name, first_name, patronymic, gender, citizenship, birth_date, birth_place, id_doc_type, id_doc_series, id_doc_number, id_doc_issued_date, id_doc_issued_by, id_doc_department_code, registration_address, residential_address, inn, snils, email, phone, invested_other_platforms) VALUES
    (300, 300,
     'Сидорова', 'Елена', 'Александровна',
     'FEMALE', 'Российская Федерация', '1990-08-25', 'г. Москва',
     'Паспорт гражданина РФ', '4520', '654321', '2015-04-10',
     'Отделением УФМС России по г. Москве по району Тверской', '770-120',
     'г. Москва, ул. Пушкина, д. 10, кв. 45',
     'г. Москва, ул. Пушкина, д. 10, кв. 45',
     '772345678901', '123-456-789 01',
     'investor.fl@investplatform.ru', '+79161234567', 150000.00),
    (303, 303,
     'Морозов', 'Александр', 'Викторович',
     'MALE', 'Российская Федерация', '1987-04-12', 'г. Нижний Новгород',
     'Паспорт гражданина РФ', '2218', '334455', '2017-06-20',
     'Отделом УФМС России по Нижегородской области в Нижегородском районе', '520-015',
     'г. Нижний Новгород, ул. Большая Покровская, д. 22, кв. 7',
     'г. Нижний Новгород, ул. Большая Покровская, д. 22, кв. 7',
     '525678901234', '234-567-890 12',
     'morozov.a@investplatform.ru', '+79201112233', 300000.00),
    (304, 304,
     'Николаева', 'Дарья', 'Сергеевна',
     'FEMALE', 'Российская Федерация', '1983-12-01', 'г. Санкт-Петербург',
     'Паспорт гражданина РФ', '4010', '778899', '2013-02-14',
     'Отделом УФМС России по г. Санкт-Петербургу в Адмиралтейском районе', '780-004',
     'г. Санкт-Петербург, Невский пр., д. 88, кв. 201',
     'г. Санкт-Петербург, Невский пр., д. 88, кв. 201',
     '784567890123', '345-678-901 23',
     'nikolaeva.d@investplatform.ru', '+79111223344', 750000.00),
    (307, 307,
     'Белов', 'Максим', 'Дмитриевич',
     'MALE', 'Российская Федерация', '1995-06-18', 'г. Краснодар',
     'Паспорт гражданина РФ', '0316', '223344', '2019-08-05',
     'Отделом МВД России по Краснодарскому краю в Западном округе г. Краснодара', '230-012',
     'г. Краснодар, ул. Красная, д. 150, кв. 33',
     'г. Краснодар, ул. Красная, д. 150, кв. 33',
     '231234567890', '456-789-012 34',
     'belov.m@investplatform.ru', '+79181234567', 50000.00),
    (308, 308,
     'Тихонова', 'Анна', 'Петровна',
     'FEMALE', 'Российская Федерация', '1998-02-28', 'г. Воронеж',
     'Паспорт гражданина РФ', '2014', '556677', '2018-03-12',
     'Отделом УФМС России по Воронежской области в Центральном районе г. Воронежа', '360-005',
     'г. Воронеж, ул. Плехановская, д. 40, кв. 12',
     'г. Воронеж, ул. Плехановская, д. 40, кв. 12',
     '366789012345', '567-890-123 45',
     'tikhonova.a@investplatform.ru', '+79521234567', 0.00);

SELECT setval('investor_individuals_id_seq', (SELECT MAX(id) FROM investor_individuals));

--changeset investplatform:002-mock-investor-pe
INSERT INTO investor_private_entrepreneurs (id, investor_id, last_name, first_name, patronymic, gender, citizenship, birth_date, birth_place, id_doc_type, id_doc_series, id_doc_number, id_doc_issued_date, id_doc_issued_by, id_doc_department_code, registration_address, residential_address, inn, snils, ogrnip, email, phone) VALUES
    (301, 301,
     'Волков', 'Игорь', 'Дмитриевич',
     'MALE', 'Российская Федерация', '1988-11-03', 'г. Екатеринбург',
     'Паспорт гражданина РФ', '6510', '987654', '2018-09-20',
     'ТП №3 ОУФМС России по Свердловской обл. в Ленинском р-не г. Екатеринбурга', '660-003',
     'г. Екатеринбург, ул. Малышева, д. 56, кв. 12',
     'г. Екатеринбург, ул. Малышева, д. 56, кв. 12',
     '667890123456', '987-654-321 09', '319665800012345',
     'investor.ip@investplatform.ru', '+79221234567'),
    (305, 305,
     'Павлов', 'Константин', 'Андреевич',
     'MALE', 'Российская Федерация', '1992-09-14', 'г. Самара',
     'Паспорт гражданина РФ', '3612', '112233', '2016-11-30',
     'Отделом УФМС России по Самарской области в Ленинском районе г. Самары', '630-008',
     'г. Самара, ул. Молодогвардейская, д. 78, кв. 5',
     'г. Самара, ул. Молодогвардейская, д. 78, кв. 5',
     '631234567890', '678-901-234 56', '321631500067890',
     'pavlov.k@investplatform.ru', '+79271234567');

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
     '+74951234567'),
    (306, 306,
     'Общество с ограниченной ответственностью "КапиталИнвест"',
     'ООО "КапиталИнвест"',
     '1127747890123',
     '7747012345',
     'г. Москва, ул. Новый Арбат, д. 36, стр. 3, оф. 710',
     'г. Москва, ул. Новый Арбат, д. 36, стр. 3, оф. 710',
     'kapitalinvest@investplatform.ru',
     '+74959876543'),
    (309, 309,
     'Акционерное общество "ВенчурФонд"',
     'АО "ВенчурФонд"',
     '1187746789012',
     '7702345678',
     'г. Москва, Ленинградский пр., д. 39, стр. 79, оф. 1502',
     'г. Москва, Ленинградский пр., д. 39, стр. 79, оф. 1502',
     'venchurfond@investplatform.ru',
     '+74955554433');

SELECT setval('investor_legal_entities_id_seq', (SELECT MAX(id) FROM investor_legal_entities));

--changeset investplatform:002-mock-investor-le-executive
INSERT INTO investor_le_executives (id, investor_legal_entity_id, is_management_company, last_name, first_name, patronymic, gender, citizenship, birth_date, birth_place, id_doc_type, id_doc_series, id_doc_number, id_doc_issued_date, id_doc_issued_by, id_doc_department_code, registration_address, residential_address, inn, email, phone) VALUES
    (1, 302, FALSE,
     'Громов', 'Виктор', 'Анатольевич',
     'MALE', 'Российская Федерация', '1975-01-30', 'г. Москва',
     'Паспорт гражданина РФ', '4508', '112233', '2008-06-15',
     'Отделом УФМС России по г. Москве по Замоскворецкому р-ну', '770-045',
     'г. Москва, ул. Якиманка, д. 5, кв. 78',
     'г. Москва, ул. Якиманка, д. 5, кв. 78',
     '770567890123',
     'gromov@investgrupp.ru', '+79031234567'),
    (2, 306, FALSE,
     'Орлов', 'Павел', 'Николаевич',
     'MALE', 'Российская Федерация', '1980-05-20', 'г. Москва',
     'Паспорт гражданина РФ', '4515', '445566', '2010-12-01',
     'Отделением УФМС России по г. Москве по району Арбат', '770-060',
     'г. Москва, ул. Арбат, д. 30, кв. 15',
     'г. Москва, ул. Арбат, д. 30, кв. 15',
     '770678901234',
     'orlov@kapitalinvest.ru', '+79051112233'),
    (3, 309, FALSE,
     'Данилов', 'Артём', 'Игоревич',
     'MALE', 'Российская Федерация', '1978-10-10', 'г. Москва',
     'Паспорт гражданина РФ', '4512', '667788', '2012-07-18',
     'Отделом УФМС России по г. Москве по Савёловскому р-ну', '770-080',
     'г. Москва, Ленинградский пр., д. 45, кв. 200',
     'г. Москва, Ленинградский пр., д. 45, кв. 200',
     '770789012345',
     'danilov@venchurfond.ru', '+79031234588');

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
    -- ООО "ТехноИнвест" (200)
    (1, 'TINV-001', 1, 1, 'Обыкновенная акция',    '1-01-12345-А', '2025-07-15', 'RU000A106K11', 'RUB', 100.0000,  100000.0000, 25000.0000,  'именная', 'ESVUFR', 'EQUITY', 200),
    (2, 'TINV-002', 2, 3, 'Облигация купонная',     '4-01-12345-А', '2025-10-01', 'RU000A107B22', 'RUB', 1000.0000,  50000.0000, 10000.0000,  'именная', 'DBFUFR', 'BOND',   200),
    -- ИП Петров (201)
    (3, 'PETR-001', 1, 1, 'Обыкновенная акция',     '1-02-67890-А', '2025-11-10', 'RU000A108C33', 'RUB', 500.0000,   20000.0000,  5000.0000,  'именная', 'ESVUFR', 'EQUITY', 201),
    -- АО "СтройИнновация" (202)
    (4, 'STRN-001', 1, 1, 'Обыкновенная акция',     '1-03-11111-А', '2025-09-01', 'RU000A109D44', 'RUB', 200.0000,   50000.0000, 15000.0000,  'именная', 'ESVUFR', 'EQUITY', 202),
    (5, 'STRN-002', 2, 3, 'Облигация купонная',     '4-03-11111-А', '2025-12-01', 'RU000A110E55', 'RUB', 1000.0000,  30000.0000,     0.0000,  'именная', 'DBFUFR', 'BOND',   202),
    -- ООО "АгроТех" (203)
    (6, 'AGRT-001', 1, 1, 'Обыкновенная акция',     '1-04-22222-А', '2025-10-15', 'RU000A111F66', 'RUB', 150.0000,   80000.0000, 20000.0000,  'именная', 'ESVUFR', 'EQUITY', 203),
    -- ИП Кузнецова (204)
    (7, 'KUZN-001', 1, 1, 'Обыкновенная акция',     '1-05-33333-А', '2026-01-10', 'RU000A112G77', 'RUB', 300.0000,   15000.0000,     0.0000,  'именная', 'ESVUFR', 'EQUITY', 204),
    -- ООО "МедТехника" (205)
    (8, 'MEDT-001', 1, 1, 'Обыкновенная акция',     '1-06-44444-А', '2025-11-20', 'RU000A113H88', 'RUB', 500.0000,   40000.0000, 10000.0000,  'именная', 'ESVUFR', 'EQUITY', 205),
    (9, 'MEDT-002', 1, 2, 'Привилегированная акция', '2-06-44444-А', '2025-11-20', 'RU000A114I99', 'RUB', 1000.0000,  10000.0000, 10000.0000,  'именная', 'EPNUFR', 'EQUITY', 205);

SELECT setval('securities_id_seq', (SELECT MAX(id) FROM securities));

--changeset investplatform:002-mock-proposals
-- status_id: 1=draft, 2=pending, 3=reviewing, 4=rejected, 5=active, 6=failed, 7=completed

-- Proposal 1: Active (ООО ТехноИнвест — акции)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    issue_decision_info, placement_procedure, placement_terms, placement_conditions,
    has_preemptive_right, risk_warning,
    max_investment_amount, min_investment_amount,
    price_per_unit, total_quantity, min_purchase_quantity, max_purchase_quantity,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    reviewed_by, submitted_at, reviewed_at, activated_at) VALUES
    (1, 200, 5, 1, 1,
     'Размещение акций ООО "ТехноИнвест" — серия А',
     'Привлечение инвестиций для масштабирования SaaS-платформы управления данными. Средства будут направлены на расширение команды разработки и маркетинг.',
     'Неуспешное масштабирование продукта, изменение рыночной конъюнктуры, появление конкурентов с аналогичным продуктом.',
     'Риск потери ключевых сотрудников, зависимость от единственного продукта, ограниченная история операционной деятельности.',
     'Риск невозврата инвестиций, риск размытия доли при последующих раундах, отсутствие дивидендной политики на ранней стадии.',
     'Решение единственного участника №5 от 10.06.2025 о размещении дополнительных акций.',
     'Размещение осуществляется через инвестиционную платформу путём заключения договоров инвестирования.',
     'Срок размещения — 120 дней с момента публикации. Акции размещаются по номинальной стоимости 100 руб. за акцию.',
     'Размещение акций на условиях, указанных в решении о выпуске.',
     FALSE,
     'ВНИМАНИЕ: Инвестирование в ценные бумаги сопряжено с риском потери всей суммы инвестиций. Перед принятием решения внимательно ознакомьтесь с рисками.',
     10000000.00, 10000.00,
     100.0000, 100000, 100, 100000,
     '2026-02-01', '2026-06-01',
     'Инвестор приобретает обыкновенные именные акции по цене 100 руб. за акцию. Минимальная сумма инвестирования — 10 000 руб.',
     'Российская Федерация',
     2700000.00,
     100,
     '2026-01-20 09:00:00', '2026-01-25 14:00:00', '2026-02-01 00:00:00');

-- Proposal 2: Draft (ООО ТехноИнвест — облигации)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    risk_warning,
    max_investment_amount, min_investment_amount,
    price_per_unit, total_quantity, min_purchase_quantity, max_purchase_quantity,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount) VALUES
    (2, 200, 1, 1, 2,
     'Облигационный заём ООО "ТехноИнвест" — купонные облигации',
     'Привлечение заёмного финансирования для закупки серверного оборудования и развития инфраструктуры.',
     'Технологические риски, риск задержки поставок оборудования.',
     'Кредитный риск эмитента, риск невозврата основного долга.',
     'Процентный риск, риск дефолта эмитента, риск невозможности досрочного погашения.',
     'Инвестирование связано с риском потери инвестированных средств. Эмитент не гарантирует доходность вложений.',
     25000000.00, 50000.00,
     1000.0000, 50000, 50, 25000,
     '2026-05-01', '2026-08-01',
     'Купонная облигация номиналом 1000 руб. Купонный период — 6 месяцев. Ставка купона — 14% годовых.',
     'Российская Федерация',
     0.00);

-- Proposal 3: Active (ИП Петров — акции)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    risk_warning,
    max_investment_amount, min_investment_amount,
    price_per_unit, total_quantity, min_purchase_quantity, max_purchase_quantity,
    proposal_start_date, proposal_end_date,
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
     500.0000, 20000, 10, 10000,
     '2026-03-01', '2026-06-30',
     'Инвестор приобретает обыкновенные именные акции по цене 500 руб. за акцию. Минимальная сумма инвестирования — 5 000 руб.',
     'Российская Федерация',
     825000.00,
     101,
     '2026-02-10 11:00:00', '2026-02-15 16:00:00', '2026-03-01 00:00:00');

-- Proposal 4: Rejected (ООО ТехноИнвест — IoT)
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
     '2025-12-01', '2026-03-01',
     'Договор займа с конвертацией в долю.',
     'Российская Федерация',
     0.00,
     100,
     'Недостаточно проработан бизнес-план. Отсутствуют финансовые прогнозы и анализ рынка. Необходимо доработать документацию и предоставить MVP продукта.',
     '2025-11-01 10:00:00', '2025-11-10 15:00:00');

-- Proposal 5: Active (АО СтройИнновация — акции)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    issue_decision_info, placement_procedure, placement_terms, placement_conditions,
    has_preemptive_right, risk_warning,
    max_investment_amount, min_investment_amount,
    price_per_unit, total_quantity, min_purchase_quantity, max_purchase_quantity,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    reviewed_by, submitted_at, reviewed_at, activated_at) VALUES
    (5, 202, 5, 1, 4,
     'Размещение акций АО "СтройИнновация" — раунд B',
     'Привлечение финансирования для строительства ЖК "Зелёный квартал" (230 квартир) и модернизации проектного отдела.',
     'Задержки согласований и разрешений, рост цен на строительные материалы, снижение спроса на жильё.',
     'Зависимость от строительной конъюнктуры, высокая долговая нагрузка, географическая концентрация проектов в одном регионе.',
     'Риск затоваривания на рынке новостроек, риск недостаточного спроса, валютные риски при закупке импортного оборудования.',
     'Решение Совета директоров №12 от 15.08.2025 о размещении дополнительных акций.',
     'Размещение через инвестиционную платформу путём заключения договоров инвестирования.',
     'Акции размещаются по номинальной стоимости 200 руб. Срок размещения — 120 дней.',
     'На условиях решения о выпуске и устава общества.',
     FALSE,
     'Инвестирование связано с высоким уровнем риска. Возможна полная потеря инвестиций.',
     10000000.00, 10000.00,
     200.0000, 50000, 50, 50000,
     '2026-03-01', '2026-07-01',
     'Инвестор приобретает обыкновенные именные акции по цене 200 руб. за акцию.',
     'Российская Федерация',
     500000.00,
     102,
     '2026-02-15 10:00:00', '2026-02-20 12:00:00', '2026-03-01 00:00:00');

-- Proposal 6: Draft (АО СтройИнновация — облигации)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    risk_warning,
    max_investment_amount, min_investment_amount,
    price_per_unit, total_quantity, min_purchase_quantity, max_purchase_quantity,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount) VALUES
    (6, 202, 1, 1, 5,
     'Облигации АО "СтройИнновация" — строительный заём',
     'Рефинансирование кредитной линии и финансирование строительства ЖК "Парковый".',
     'Процентный риск, риск пролонгации строительных сроков.',
     'Кредитный риск, зависимость от банковского финансирования.',
     'Риск дефолта по купонным выплатам при ухудшении рыночной конъюнктуры.',
     'Существует риск полной или частичной потери инвестированных средств.',
     30000000.00, 100000.00,
     1000.0000, 30000, 100, 30000,
     '2026-07-01', '2026-10-01',
     'Купонная облигация номиналом 1000 руб. Ставка купона — 16% годовых. Купонный период — 3 месяца.',
     'Российская Федерация',
     0.00);

-- Proposal 7: Active (ООО АгроТех — акции)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    issue_decision_info, placement_procedure, placement_terms, placement_conditions,
    has_preemptive_right, risk_warning,
    max_investment_amount, min_investment_amount,
    price_per_unit, total_quantity, min_purchase_quantity, max_purchase_quantity,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    reviewed_by, submitted_at, reviewed_at, activated_at) VALUES
    (7, 203, 5, 1, 6,
     'Размещение акций ООО "АгроТех" — раунд Seed',
     'Масштабирование IoT-платформы точного земледелия на 50 фермерских хозяйств Сибири и Урала. Доработка AI-модуля прогнозирования урожайности.',
     'Сезонность сельского хозяйства, зависимость от погодных условий, низкая цифровая грамотность конечных пользователей.',
     'Стартап на ранней стадии, ограниченная выручка, зависимость от грантового финансирования.',
     'Технологические риски, риск невозврата, длительный цикл окупаемости (3-5 лет).',
     'Решение общего собрания участников №3 от 01.09.2025.',
     'Через инвестиционную платформу.',
     'Акции по 150 руб., срок размещения — 120 дней.',
     'На условиях решения о выпуске.',
     FALSE,
     'Высокий уровень риска. Стартап может не выйти на окупаемость.',
     12000000.00, 15000.00,
     150.0000, 80000, 100, 80000,
     '2026-02-15', '2026-06-15',
     'Обыкновенные акции по цене 150 руб. Мин. инвестиция — 15 000 руб.',
     'Российская Федерация',
     600000.00,
     103,
     '2026-02-01 09:00:00', '2026-02-10 14:00:00', '2026-02-15 00:00:00');

-- Proposal 8: Pending (ИП Кузнецова — акции)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    risk_warning,
    max_investment_amount, min_investment_amount,
    price_per_unit, total_quantity, min_purchase_quantity, max_purchase_quantity,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    submitted_at) VALUES
    (8, 204, 2, 1, 7,
     'Размещение акций сети "КофеЛаб" (ИП Кузнецова М.В.)',
     'Открытие 8 новых кофеен в Самаре и Уфе, запуск обжарочного производства.',
     'Высокая конкуренция в сегменте HoReCa, рост стоимости аренды.',
     'Зависимость от ключевого лица (ИП), отсутствие диверсификации бизнеса.',
     'Операционные риски, сезонность потребления, риск неудачной локации новых точек.',
     'Инвестиции не застрахованы. Высокий уровень риска.',
     4500000.00, 5000.00,
     300.0000, 15000, 10, 15000,
     '2026-05-01', '2026-08-01',
     'Обыкновенные акции по 300 руб. Мин. инвестиция — 5 000 руб.',
     'Российская Федерация',
     0.00,
     '2026-03-28 10:00:00');

-- Proposal 9: Active (ООО МедТехника — акции обыкновенные)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    issue_decision_info, placement_procedure, placement_terms, placement_conditions,
    has_preemptive_right, risk_warning,
    max_investment_amount, min_investment_amount,
    price_per_unit, total_quantity, min_purchase_quantity, max_purchase_quantity,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    reviewed_by, submitted_at, reviewed_at, activated_at) VALUES
    (9, 205, 5, 1, 8,
     'Размещение акций ООО "МедТехника" — серия А',
     'Разработка и сертификация портативного УЗИ-сканера нового поколения. Выход на рынки СНГ.',
     'Регуляторные риски (длительная сертификация), высокая стоимость R&D.',
     'Зависимость от результатов клинических испытаний, конкуренция с международными производителями.',
     'Риск задержки вывода продукта на рынок, патентные споры, валютные риски.',
     'Решение общего собрания участников №7 от 01.12.2025.',
     'Через инвестиционную платформу.',
     'Акции по 500 руб. Срок размещения — 120 дней.',
     'На условиях решения о выпуске.',
     FALSE,
     'Инвестирование в медтех связано с повышенным регуляторным риском.',
     20000000.00, 50000.00,
     500.0000, 40000, 50, 40000,
     '2026-03-15', '2026-07-15',
     'Обыкновенные акции по цене 500 руб. Мин. инвестиция — 50 000 руб.',
     'Российская Федерация',
     1250000.00,
     104,
     '2026-03-01 10:00:00', '2026-03-10 11:00:00', '2026-03-15 00:00:00');

-- Proposal 10: Completed (ООО МедТехника — привилегированные акции)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, security_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    issue_decision_info, placement_procedure, placement_terms, placement_conditions,
    has_preemptive_right, risk_warning,
    max_investment_amount, min_investment_amount,
    price_per_unit, total_quantity, min_purchase_quantity, max_purchase_quantity,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    reviewed_by, submitted_at, reviewed_at, activated_at, closed_at) VALUES
    (10, 205, 7, 1, 9,
     'Размещение привилегированных акций ООО "МедТехника"',
     'Финансирование серийного производства сертифицированного портативного глюкометра непрерывного мониторинга.',
     'Изменение требований к медизделиям, конкурентное давление крупных игроков.',
     'Зависимость от поставщиков комплектующих, ограниченные производственные мощности.',
     'Привилегированные акции не дают права голоса. Дивиденды не гарантированы.',
     'Решение общего собрания участников №5 от 15.08.2025.',
     'Через инвестиционную платформу.',
     'Привилегированные акции по 1000 руб. Дивидендная доходность — 12% годовых.',
     'На условиях устава и решения о выпуске.',
     FALSE,
     'Инвестирование в привилегированные акции: отсутствие права голоса, дивиденды не гарантированы.',
     10000000.00, 10000.00,
     1000.0000, 10000, 10, 10000,
     '2025-10-01', '2026-01-31',
     'Привилегированные акции по 1000 руб. Фиксированный дивиденд — 120 руб. на акцию в год.',
     'Российская Федерация',
     10000000.00,
     100,
     '2025-09-15 10:00:00', '2025-09-25 14:00:00', '2025-10-01 00:00:00', '2026-01-31 23:59:59');

-- Proposal 11: Reviewing (ООО АгроТех — второй раунд)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    risk_warning,
    max_investment_amount, min_investment_amount,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    submitted_at) VALUES
    (11, 203, 3, 1,
     'Раунд А — масштабирование ООО "АгроТех" на Юг России',
     'Развёртывание платформы на юге России (Краснодарский и Ставропольский края). Адаптация AI-модели для южных культур.',
     'Различия в агроклиматических условиях, новый региональный рынок.',
     'Необходимость значительных инвестиций в адаптацию продукта.',
     'Высокий риск масштабирования в новом регионе.',
     'Повышенный уровень риска. Стартап на ранней стадии.',
     8000000.00, 10000.00,
     '2026-06-01', '2026-09-01',
     'Условия будут определены после рассмотрения.',
     'Российская Федерация',
     0.00,
     '2026-03-25 12:00:00');

-- Proposal 12: Failed (АО СтройИнновация — неудачный раунд)
INSERT INTO investment_proposals (id, emitent_id, status_id, investment_method_id, title,
    investment_goals, goal_risk_factors, emitent_risks, investment_risks,
    risk_warning,
    max_investment_amount, min_investment_amount,
    proposal_start_date, proposal_end_date,
    essential_contract_terms, applicable_law, collected_amount,
    reviewed_by, submitted_at, reviewed_at, activated_at, closed_at) VALUES
    (12, 202, 6, 1,
     'Элитный ЖК "Панорама" — первый транш',
     'Привлечение финансирования для строительства элитного жилого комплекса на 50 квартир.',
     'Узкий целевой сегмент, высокая стоимость строительства, зависимость от конъюнктуры премиального рынка.',
     'Концентрация на одном проекте, высокая себестоимость.',
     'Риск снижения спроса на элитное жильё, длительный цикл продаж.',
     'Крайне высокий уровень риска из-за узкого рыночного сегмента.',
     50000000.00, 500000.00,
     '2025-11-01', '2026-02-28',
     'Договор инвестирования в строительство элитного жилья.',
     'Российская Федерация',
     3200000.00,
     102,
     '2025-10-15 09:00:00', '2025-10-25 16:00:00', '2025-11-01 00:00:00', '2026-02-28 23:59:59');

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
    (4, 3, 4, 100, 'Отклонено. Недостаточно проработана документация.'),
    -- Proposal 5: draft -> pending -> reviewing -> active
    (5, NULL, 1, 202, 'ИП создано'),
    (5, 1, 2, 202, 'Отправлено на рассмотрение'),
    (5, 2, 3, 102, 'Принято к рассмотрению'),
    (5, 3, 5, 102, 'Документация в порядке, ИП активировано'),
    -- Proposal 6: draft
    (6, NULL, 1, 202, 'ИП создано'),
    -- Proposal 7: draft -> pending -> reviewing -> active
    (7, NULL, 1, 203, 'ИП создано'),
    (7, 1, 2, 203, 'Отправлено на рассмотрение'),
    (7, 2, 3, 103, 'Принято к рассмотрению'),
    (7, 3, 5, 103, 'Проверка пройдена, ИП активировано'),
    -- Proposal 8: draft -> pending
    (8, NULL, 1, 204, 'ИП создано'),
    (8, 1, 2, 204, 'Отправлено на рассмотрение'),
    -- Proposal 9: draft -> pending -> reviewing -> active
    (9, NULL, 1, 205, 'ИП создано'),
    (9, 1, 2, 205, 'Отправлено на рассмотрение'),
    (9, 2, 3, 104, 'Принято к рассмотрению'),
    (9, 3, 5, 104, 'Документация и финансовые показатели в норме'),
    -- Proposal 10: draft -> pending -> reviewing -> active -> completed
    (10, NULL, 1, 205, 'ИП создано'),
    (10, 1, 2, 205, 'Отправлено на рассмотрение'),
    (10, 2, 3, 100, 'Принято к рассмотрению'),
    (10, 3, 5, 100, 'ИП активировано'),
    (10, 5, 7, 100, 'Размещение полностью завершено. Собрано 10 000 000 руб.'),
    -- Proposal 11: draft -> pending -> reviewing
    (11, NULL, 1, 203, 'ИП создано'),
    (11, 1, 2, 203, 'Отправлено на рассмотрение'),
    (11, 2, 3, 103, 'Принято к рассмотрению, запрошены дополнительные документы'),
    -- Proposal 12: draft -> pending -> reviewing -> active -> failed
    (12, NULL, 1, 202, 'ИП создано'),
    (12, 1, 2, 202, 'Отправлено на рассмотрение'),
    (12, 2, 3, 102, 'Принято к рассмотрению'),
    (12, 3, 5, 102, 'ИП активировано'),
    (12, 5, 6, 102, 'Размещение не состоялось: собрано 3,2 млн из 50 млн. Недостаточный спрос на элитный сегмент.');

--changeset investplatform:002-mock-payments
INSERT INTO payments (id, yukassa_payment_id, personal_account_id, payment_type, direction, amount, currency, yukassa_status, payment_method_type, description, idempotency_key, paid_at) VALUES
    -- Investor 300 (Сидорова) deposits
    (1,  'pay_2a1b3c4d-0001', 300, 'DEPOSIT',   'INBOUND',  300000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-001', '2026-01-01 10:15:00'),
    (2,  'pay_2a1b3c4d-0002', 300, 'DEPOSIT',   'INBOUND',  250000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-002', '2026-02-10 12:00:00'),
    -- Investor 301 (Волков) deposit
    (3,  'pay_2a1b3c4d-0003', 301, 'DEPOSIT',   'INBOUND',  250000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-003', '2026-01-20 09:30:00'),
    -- Investor 302 (АО ИнвестГрупп) deposits
    (4,  'pay_2a1b3c4d-0004', 302, 'DEPOSIT',   'INBOUND', 2000000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-004', '2025-12-01 08:00:00'),
    (5,  'pay_2a1b3c4d-0005', 302, 'DEPOSIT',   'INBOUND', 1200000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-005', '2026-02-05 14:20:00'),
    -- Emitent 200 deposit (from completed proposals)
    (6,  'pay_2a1b3c4d-0006', 200, 'TRANSFER',  'INBOUND', 1500000.00,  'RUB', 'succeeded', 'internal',  'Перевод средств по ИП №1', 'idem-pay-006', '2026-03-01 12:00:00'),
    -- Investor 303 (Морозов) deposits
    (7,  'pay_2a1b3c4d-0007', 303, 'DEPOSIT',   'INBOUND',  500000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-007', '2026-01-15 11:00:00'),
    (8,  'pay_2a1b3c4d-0008', 303, 'DEPOSIT',   'INBOUND',  300000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-008', '2026-02-28 15:30:00'),
    -- Investor 304 (Николаева) deposits
    (9,  'pay_2a1b3c4d-0009', 304, 'DEPOSIT',   'INBOUND',  600000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-009', '2025-12-10 10:00:00'),
    (10, 'pay_2a1b3c4d-0010', 304, 'DEPOSIT',   'INBOUND',  400000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-010', '2026-03-01 09:00:00'),
    -- Investor 305 (Павлов) deposit
    (11, 'pay_2a1b3c4d-0011', 305, 'DEPOSIT',   'INBOUND',  200000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-011', '2026-02-05 14:00:00'),
    -- Investor 306 (КапиталИнвест) deposits
    (12, 'pay_2a1b3c4d-0012', 306, 'DEPOSIT',   'INBOUND', 3000000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-012', '2025-11-01 08:30:00'),
    (13, 'pay_2a1b3c4d-0013', 306, 'DEPOSIT',   'INBOUND', 2000000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-013', '2026-01-20 16:00:00'),
    -- Investor 307 (Белов) deposit
    (14, 'pay_2a1b3c4d-0014', 307, 'DEPOSIT',   'INBOUND',  150000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-014', '2026-02-25 12:00:00'),
    -- Investor 308 (Тихонова) deposit
    (15, 'pay_2a1b3c4d-0015', 308, 'DEPOSIT',   'INBOUND',  100000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-015', '2026-03-01 10:00:00'),
    -- Investor 309 (ВенчурФонд) deposits
    (16, 'pay_2a1b3c4d-0016', 309, 'DEPOSIT',   'INBOUND', 3000000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-016', '2025-10-01 09:00:00'),
    (17, 'pay_2a1b3c4d-0017', 309, 'DEPOSIT',   'INBOUND', 2000000.00,  'RUB', 'succeeded', 'bank_card', 'Пополнение счёта', 'idem-pay-017', '2026-02-15 11:00:00'),
    -- Emitent 202 (СтройИнновация) transfer
    (18, 'pay_2a1b3c4d-0018', 202, 'TRANSFER',  'INBOUND', 2000000.00,  'RUB', 'succeeded', 'internal',  'Перевод средств по ИП №10', 'idem-pay-018', '2026-02-01 10:00:00'),
    -- Emitent 203 (АгроТех) transfer
    (19, 'pay_2a1b3c4d-0019', 203, 'TRANSFER',  'INBOUND',  800000.00,  'RUB', 'succeeded', 'internal',  'Перевод средств по ИП №7',  'idem-pay-019', '2026-03-20 10:00:00'),
    -- Emitent 205 (МедТехника) transfer
    (20, 'pay_2a1b3c4d-0020', 205, 'TRANSFER',  'INBOUND', 3000000.00,  'RUB', 'succeeded', 'internal',  'Перевод средств по ИП №10', 'idem-pay-020', '2026-02-01 11:00:00'),
    -- Failed payment (investor 307 — cancelled)
    (21, 'pay_2a1b3c4d-0021', 307, 'DEPOSIT',   'INBOUND',   50000.00,  'RUB', 'canceled',  'bank_card', 'Пополнение счёта (отменён)', 'idem-pay-021', NULL),
    -- Pending payment (investor 308)
    (22, 'pay_2a1b3c4d-0022', 308, 'DEPOSIT',   'INBOUND',   50000.00,  'RUB', 'pending',   'bank_card', 'Пополнение счёта', 'idem-pay-022', NULL);

SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));

--changeset investplatform:002-mock-account-transactions
INSERT INTO account_transactions (personal_account_id, transaction_type, amount, balance_after, payment_id, description) VALUES
    -- Investor 300 (Сидорова)
    (300, 'DEPOSIT',  300000.00,  300000.00, 1,    'Пополнение счёта'),
    (300, 'DEPOSIT',  250000.00,  550000.00, 2,    'Пополнение счёта'),
    (300, 'HOLD',      50000.00,  550000.00, NULL, 'Резервирование средств по ДИ'),
    -- Investor 301 (Волков)
    (301, 'DEPOSIT',  250000.00,  250000.00, 3,    'Пополнение счёта'),
    -- Investor 302 (АО ИнвестГрупп)
    (302, 'DEPOSIT', 2000000.00, 2000000.00, 4,    'Пополнение счёта'),
    (302, 'DEPOSIT', 1200000.00, 3200000.00, 5,    'Пополнение счёта'),
    (302, 'HOLD',     200000.00, 3200000.00, NULL, 'Резервирование средств по ДИ'),
    -- Emitent 200 (ТехноИнвест)
    (200, 'DEPOSIT', 1500000.00, 1500000.00, 6,    'Перевод средств по ИП №1'),
    -- Investor 303 (Морозов)
    (303, 'DEPOSIT',  500000.00,  500000.00, 7,    'Пополнение счёта'),
    (303, 'DEPOSIT',  300000.00,  800000.00, 8,    'Пополнение счёта'),
    (303, 'HOLD',     200000.00,  800000.00, NULL, 'Резервирование средств по ДИ №4 и №8'),
    -- Investor 304 (Николаева)
    (304, 'DEPOSIT',  600000.00,  600000.00, 9,    'Пополнение счёта'),
    (304, 'DEPOSIT',  400000.00, 1000000.00, 10,   'Пополнение счёта'),
    (304, 'HOLD',     125000.00, 1000000.00, NULL, 'Резервирование средств по ДИ №6 и №9'),
    -- Investor 305 (Павлов)
    (305, 'DEPOSIT',  200000.00,  200000.00, 11,   'Пополнение счёта'),
    -- Investor 306 (КапиталИнвест)
    (306, 'DEPOSIT', 3000000.00, 3000000.00, 12,   'Пополнение счёта'),
    (306, 'DEPOSIT', 2000000.00, 5000000.00, 13,   'Пополнение счёта'),
    (306, 'HOLD',     430000.00, 5000000.00, NULL, 'Резервирование средств по ДИ №5 и №14'),
    -- Investor 307 (Белов)
    (307, 'DEPOSIT',  150000.00,  150000.00, 14,   'Пополнение счёта'),
    -- Investor 308 (Тихонова)
    (308, 'DEPOSIT',  100000.00,  100000.00, 15,   'Пополнение счёта'),
    -- Investor 309 (ВенчурФонд)
    (309, 'DEPOSIT', 3000000.00, 3000000.00, 16,   'Пополнение счёта'),
    (309, 'DEPOSIT', 2000000.00, 5000000.00, 17,   'Пополнение счёта'),
    (309, 'HOLD',     400000.00, 5000000.00, NULL, 'Резервирование средств по ДИ №10'),
    -- Emitent 202 (СтройИнновация)
    (202, 'DEPOSIT', 2000000.00, 2000000.00, 18,   'Перевод средств по ИП №10'),
    -- Emitent 203 (АгроТех)
    (203, 'DEPOSIT',  800000.00,  800000.00, 19,   'Перевод средств по ИП №7'),
    -- Emitent 205 (МедТехника)
    (205, 'DEPOSIT', 3000000.00, 3000000.00, 20,   'Перевод средств по ИП №10');

--changeset investplatform:002-mock-contracts
-- contract_status_id: 1=reviewing, 2=withdrawn, 3=rejected, 4=approved, 5=completed, 6=failed

-- Contracts for proposal 1 (ТехноИнвест акции)
INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, payment_id, signed_at, reviewed_at, created_at) VALUES
    (1, 'DI-2026-000001', 1, 300, 4,  50000.00, 1,  500.0000, 100.0000, 100, 2, '2026-02-15 10:00:00', '2026-02-16 11:00:00', '2026-02-15 10:00:00'),
    (2, 'DI-2026-000002', 1, 302, 4, 200000.00, 1, 2000.0000, 100.0000, 100, 5, '2026-02-20 15:00:00', '2026-02-21 10:00:00', '2026-02-20 15:00:00'),
    (3, 'DI-2026-000003', 1, 303, 4, 100000.00, 1, 1000.0000, 100.0000, 102, NULL, '2026-02-25 14:00:00', '2026-02-26 09:00:00', '2026-02-25 14:00:00'),
    (4, 'DI-2026-000004', 1, 306, 1,  50000.00, 1,  500.0000, 100.0000, NULL, NULL, '2026-03-28 10:00:00', NULL, '2026-03-28 10:00:00');

-- Contracts for proposal 3 (ЭкоПак)
INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, signed_at, reviewed_at, created_at) VALUES
    (5, 'DI-2026-000005', 3, 300, 1,  50000.00, 3, 100.0000, 500.0000, NULL, '2026-03-05 09:00:00', NULL, '2026-03-05 09:00:00'),
    (6, 'DI-2026-000006', 3, 304, 4,  25000.00, 3,  50.0000, 500.0000, 103, '2026-03-10 11:00:00', '2026-03-11 14:00:00', '2026-03-10 11:00:00');

INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, signed_at, withdrawn_at, withdrawal_reason, created_at) VALUES
    (7, 'DI-2026-000007', 3, 307, 2,  15000.00, 3,  30.0000, 500.0000, '2026-03-12 10:00:00', '2026-03-15 16:00:00', 'Изменение инвестиционной стратегии, перераспределение средств в другой проект.', '2026-03-12 10:00:00');

-- Contracts for proposal 5 (СтройИнновация акции)
INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, signed_at, reviewed_at, created_at) VALUES
    (8,  'DI-2026-000008', 5, 303, 4, 200000.00, 4, 1000.0000, 200.0000, 102, '2026-03-10 09:00:00', '2026-03-12 11:00:00', '2026-03-10 09:00:00'),
    (9,  'DI-2026-000009', 5, 304, 4, 100000.00, 4,  500.0000, 200.0000, 102, '2026-03-15 10:00:00', '2026-03-16 15:00:00', '2026-03-15 10:00:00'),
    (10, 'DI-2026-000010', 5, 309, 1, 400000.00, 4, 2000.0000, 200.0000, NULL, '2026-03-30 12:00:00', NULL, '2026-03-30 12:00:00');

-- Contracts for proposal 7 (АгроТех акции)
INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, signed_at, reviewed_at, created_at) VALUES
    (11, 'DI-2026-000011', 7, 302, 4, 300000.00, 6, 2000.0000, 150.0000, 103, '2026-03-01 10:00:00', '2026-03-03 11:00:00', '2026-03-01 10:00:00'),
    (12, 'DI-2026-000012', 7, 305, 4,  75000.00, 6,  500.0000, 150.0000, 103, '2026-03-05 09:00:00', '2026-03-06 14:00:00', '2026-03-05 09:00:00');

INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, signed_at, reviewed_at, failed_at, created_at) VALUES
    (13, 'DI-2026-000013', 7, 307, 6,  45000.00, 6,  300.0000, 150.0000, 103, '2026-03-02 11:00:00', '2026-03-04 10:00:00', '2026-04-01 00:00:00', '2026-03-02 11:00:00');

-- Contracts for proposal 9 (МедТехника обыкновенные акции)
INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, signed_at, reviewed_at, created_at) VALUES
    (14, 'DI-2026-000014', 9, 303, 4, 500000.00, 8, 1000.0000, 500.0000, 104, '2026-03-20 10:00:00', '2026-03-22 11:00:00', '2026-03-20 10:00:00');

INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, rejection_reason, signed_at, reviewed_at, created_at) VALUES
    (15, 'DI-2026-000015', 9, 306, 3, 250000.00, 8,  500.0000, 500.0000, 104, 'Превышен лимит инвестирования данного инвестора для текущего календарного года.', '2026-03-21 10:00:00', '2026-03-23 15:00:00', '2026-03-21 10:00:00');

-- Contracts for proposal 10 (МедТехника привилегированные — completed)
INSERT INTO investment_contracts (id, contract_number, proposal_id, investor_id, status_id, amount, security_id, securities_quantity, price_per_security, reviewed_by, signed_at, reviewed_at, completed_at, created_at) VALUES
    (16, 'DI-2025-000016', 10, 304, 5,  500000.00, 9,  500.0000, 1000.0000, 100, '2025-10-10 10:00:00', '2025-10-12 11:00:00', '2026-01-31 23:59:59', '2025-10-10 10:00:00'),
    (17, 'DI-2025-000017', 10, 309, 5, 1500000.00, 9, 1500.0000, 1000.0000, 100, '2025-10-15 14:00:00', '2025-10-17 10:00:00', '2026-01-31 23:59:59', '2025-10-15 14:00:00');

SELECT setval('investment_contracts_id_seq', (SELECT MAX(id) FROM investment_contracts));

--changeset investplatform:002-mock-contract-status-history
INSERT INTO contract_status_history (contract_id, old_status_id, new_status_id, changed_by, comment) VALUES
    -- Contract 1 (approved)
    (1, NULL, 1, 300, 'ДИ создан'),
    (1, 1, 4, 100, 'ДИ одобрен оператором'),
    -- Contract 2 (approved)
    (2, NULL, 1, 302, 'ДИ создан'),
    (2, 1, 4, 100, 'ДИ одобрен оператором'),
    -- Contract 3 (approved)
    (3, NULL, 1, 303, 'ДИ создан'),
    (3, 1, 4, 102, 'ДИ одобрен'),
    -- Contract 4 (reviewing)
    (4, NULL, 1, 306, 'ДИ создан'),
    -- Contract 5 (reviewing)
    (5, NULL, 1, 300, 'ДИ создан'),
    -- Contract 6 (approved)
    (6, NULL, 1, 304, 'ДИ создан'),
    (6, 1, 4, 103, 'ДИ одобрен'),
    -- Contract 7 (withdrawn)
    (7, NULL, 1, 307, 'ДИ создан'),
    (7, 1, 2, 307, 'Инвестор отозвал ДИ. Причина: перераспределение средств.'),
    -- Contract 8 (approved)
    (8, NULL, 1, 303, 'ДИ создан'),
    (8, 1, 4, 102, 'ДИ одобрен'),
    -- Contract 9 (approved)
    (9, NULL, 1, 304, 'ДИ создан'),
    (9, 1, 4, 102, 'ДИ одобрен'),
    -- Contract 10 (reviewing)
    (10, NULL, 1, 309, 'ДИ создан'),
    -- Contract 11 (approved)
    (11, NULL, 1, 302, 'ДИ создан'),
    (11, 1, 4, 103, 'ДИ одобрен'),
    -- Contract 12 (approved)
    (12, NULL, 1, 305, 'ДИ создан'),
    (12, 1, 4, 103, 'ДИ одобрен'),
    -- Contract 13 (failed)
    (13, NULL, 1, 307, 'ДИ создан'),
    (13, 1, 4, 103, 'ДИ одобрен'),
    (13, 4, 6, 103, 'Инвестор не осуществил оплату в установленный срок.'),
    -- Contract 14 (approved)
    (14, NULL, 1, 303, 'ДИ создан'),
    (14, 1, 4, 104, 'ДИ одобрен'),
    -- Contract 15 (rejected)
    (15, NULL, 1, 306, 'ДИ создан'),
    (15, 1, 3, 104, 'Отклонено: превышен лимит инвестирования.'),
    -- Contract 16 (completed)
    (16, NULL, 1, 304, 'ДИ создан'),
    (16, 1, 4, 100, 'ДИ одобрен'),
    (16, 4, 5, 100, 'Размещение завершено, акции зачислены на счёт инвестора.'),
    -- Contract 17 (completed)
    (17, NULL, 1, 309, 'ДИ создан'),
    (17, 1, 4, 100, 'ДИ одобрен'),
    (17, 4, 5, 100, 'Размещение завершено, акции зачислены на счёт инвестора.');

--changeset investplatform:002-mock-security-balances
INSERT INTO security_balances (id, personal_account_id, security_id, quantity, quantity_blocked, quantity_pledged, quantity_encumbered) VALUES
    -- Security 1 (TINV-001): emitent 200 + investors 300, 302, 303
    (1,  300, 1,   500.0000, 0.0000, 0.0000, 0.0000),
    (2,  302, 1,  2000.0000, 0.0000, 0.0000, 0.0000),
    (3,  200, 1, 71500.0000, 0.0000, 0.0000, 0.0000),
    (4,  303, 1,  1000.0000, 0.0000, 0.0000, 0.0000),
    -- Security 2 (TINV-002): emitent 200
    (5,  200, 2, 40000.0000, 0.0000, 0.0000, 0.0000),
    -- Security 3 (PETR-001): emitent 201 + investor 304
    (6,  201, 3, 14950.0000, 0.0000, 0.0000, 0.0000),
    (7,  304, 3,    50.0000, 0.0000, 0.0000, 0.0000),
    -- Security 4 (STRN-001): emitent 202 + investors 303, 304
    (8,  202, 4, 33500.0000, 0.0000, 0.0000, 0.0000),
    (9,  303, 4,  1000.0000, 0.0000, 0.0000, 0.0000),
    (10, 304, 4,   500.0000, 0.0000, 0.0000, 0.0000),
    -- Security 5 (STRN-002): emitent 202 (nothing placed yet, draft)
    (11, 202, 5, 30000.0000, 0.0000, 0.0000, 0.0000),
    -- Security 6 (AGRT-001): emitent 203 + investors 302, 305
    (12, 203, 6, 57500.0000, 0.0000, 0.0000, 0.0000),
    (13, 302, 6,  2000.0000, 0.0000, 0.0000, 0.0000),
    (14, 305, 6,   500.0000, 0.0000, 0.0000, 0.0000),
    -- Security 7 (KUZN-001): emitent 204 (pending proposal, nothing placed)
    (15, 204, 7, 15000.0000, 0.0000, 0.0000, 0.0000),
    -- Security 8 (MEDT-001): emitent 205 + investor 303
    (16, 205, 8, 29000.0000, 0.0000, 0.0000, 0.0000),
    (17, 303, 8,  1000.0000, 0.0000, 0.0000, 0.0000),
    -- Security 9 (MEDT-002): investors 304, 309 (completed proposal — all placed)
    (18, 304, 9,   500.0000, 0.0000, 0.0000, 0.0000),
    (19, 309, 9,  1500.0000, 0.0000, 0.0000, 0.0000),
    (20, 205, 9,  8000.0000, 0.0000, 0.0000, 0.0000);

SELECT setval('security_balances_id_seq', (SELECT MAX(id) FROM security_balances));

--changeset investplatform:002-mock-emitent-documents
INSERT INTO emitent_documents (id, emitent_id, document_type_id, report_year, file_name, file_path, file_size, mime_type) VALUES
    -- ТехноИнвест (200)
    (1,  200, 1, 2025, 'technoinvest_fin_report_2025.pdf',  'emitents/200/financial_report_2025.pdf',  2048576, 'application/pdf'),
    (2,  200, 2, 2025, 'technoinvest_audit_2025.pdf',       'emitents/200/audit_conclusion_2025.pdf',  1536000, 'application/pdf'),
    (3,  200, 3, 2025, 'technoinvest_charter.pdf',          'emitents/200/charter.pdf',                 512000, 'application/pdf'),
    (4,  200, 4, 2025, 'technoinvest_egrul.pdf',            'emitents/200/egrul_extract_2025.pdf',      256000, 'application/pdf'),
    -- ИП Петров (201)
    (5,  201, 1, 2025, 'ecopak_fin_report_2025.pdf',        'emitents/201/financial_report_2025.pdf',   768000, 'application/pdf'),
    (6,  201, 4, 2025, 'ecopak_egrip.pdf',                  'emitents/201/egrip_extract_2025.pdf',      128000, 'application/pdf'),
    -- АО СтройИнновация (202)
    (7,  202, 1, 2025, 'stroinnovacia_fin_2025.pdf',        'emitents/202/financial_report_2025.pdf',  3145728, 'application/pdf'),
    (8,  202, 2, 2025, 'stroinnovacia_audit_2025.pdf',      'emitents/202/audit_conclusion_2025.pdf',  2097152, 'application/pdf'),
    (9,  202, 3, 2025, 'stroinnovacia_charter.pdf',         'emitents/202/charter.pdf',                 768000, 'application/pdf'),
    (10, 202, 4, 2025, 'stroinnovacia_egrul.pdf',           'emitents/202/egrul_extract_2025.pdf',      384000, 'application/pdf'),
    -- ООО АгроТех (203)
    (11, 203, 1, 2025, 'agrotech_fin_2025.pdf',             'emitents/203/financial_report_2025.pdf',  1048576, 'application/pdf'),
    (12, 203, 3, 2025, 'agrotech_charter.pdf',              'emitents/203/charter.pdf',                 512000, 'application/pdf'),
    (13, 203, 4, 2025, 'agrotech_egrul.pdf',                'emitents/203/egrul_extract_2025.pdf',      256000, 'application/pdf'),
    -- ИП Кузнецова (204)
    (14, 204, 1, 2025, 'kofelab_fin_2025.pdf',              'emitents/204/financial_report_2025.pdf',   640000, 'application/pdf'),
    (15, 204, 4, 2025, 'kofelab_egrip.pdf',                 'emitents/204/egrip_extract_2025.pdf',      128000, 'application/pdf'),
    -- ООО МедТехника (205)
    (16, 205, 1, 2025, 'medtechnika_fin_2025.pdf',          'emitents/205/financial_report_2025.pdf',  2621440, 'application/pdf'),
    (17, 205, 2, 2025, 'medtechnika_audit_2025.pdf',        'emitents/205/audit_conclusion_2025.pdf',  1572864, 'application/pdf'),
    (18, 205, 3, 2025, 'medtechnika_charter.pdf',           'emitents/205/charter.pdf',                 640000, 'application/pdf'),
    (19, 205, 4, 2025, 'medtechnika_egrul.pdf',             'emitents/205/egrul_extract_2025.pdf',      320000, 'application/pdf');

SELECT setval('emitent_documents_id_seq', (SELECT MAX(id) FROM emitent_documents));

--changeset investplatform:002-mock-investor-documents
INSERT INTO investor_documents (id, investor_id, document_type_id, report_year, file_name, file_path, file_size, mime_type) VALUES
    -- Сидорова (ФЛ, 300)
    (1,  300, 1, 2025, 'sidorova_passport_main.pdf',     'investors/300/passport_main.pdf',         1024000, 'application/pdf'),
    (2,  300, 2, 2025, 'sidorova_passport_reg.pdf',      'investors/300/passport_registration.pdf',  512000, 'application/pdf'),
    (3,  300, 4, 2025, 'sidorova_snils.pdf',             'investors/300/snils.pdf',                  256000, 'application/pdf'),
    -- Волков (ИП, 301)
    (4,  301, 1, 2025, 'volkov_passport_main.pdf',       'investors/301/passport_main.pdf',         1024000, 'application/pdf'),
    (5,  301, 5, 2025, 'volkov_ip_registration.pdf',     'investors/301/ip_registration.pdf',        384000, 'application/pdf'),
    -- АО ИнвестГрупп (ЮЛ, 302)
    (6,  302, 6, 2025, 'investgrupp_egrul.pdf',          'investors/302/le_registration.pdf',        640000, 'application/pdf'),
    (7,  302, 8, 2025, 'investgrupp_charter.pdf',        'investors/302/charter.pdf',                768000, 'application/pdf'),
    (8,  302, 9, 2025, 'investgrupp_eio_decision.pdf',   'investors/302/executive_decision.pdf',     256000, 'application/pdf'),
    -- Морозов (ФЛ, 303)
    (9,  303, 1, 2025, 'morozov_passport_main.pdf',      'investors/303/passport_main.pdf',         1024000, 'application/pdf'),
    (10, 303, 2, 2025, 'morozov_passport_reg.pdf',       'investors/303/passport_registration.pdf',  512000, 'application/pdf'),
    (11, 303, 4, 2025, 'morozov_snils.pdf',              'investors/303/snils.pdf',                  256000, 'application/pdf'),
    -- Николаева (ФЛ, 304)
    (12, 304, 1, 2025, 'nikolaeva_passport_main.pdf',    'investors/304/passport_main.pdf',         1024000, 'application/pdf'),
    (13, 304, 2, 2025, 'nikolaeva_passport_reg.pdf',     'investors/304/passport_registration.pdf',  512000, 'application/pdf'),
    (14, 304, 4, 2025, 'nikolaeva_snils.pdf',            'investors/304/snils.pdf',                  256000, 'application/pdf'),
    -- Павлов (ИП, 305)
    (15, 305, 1, 2025, 'pavlov_passport_main.pdf',       'investors/305/passport_main.pdf',         1024000, 'application/pdf'),
    (16, 305, 5, 2025, 'pavlov_ip_registration.pdf',     'investors/305/ip_registration.pdf',        384000, 'application/pdf'),
    -- КапиталИнвест (ЮЛ, 306)
    (17, 306, 6, 2025, 'kapitalinvest_egrul.pdf',        'investors/306/le_registration.pdf',        640000, 'application/pdf'),
    (18, 306, 8, 2025, 'kapitalinvest_charter.pdf',      'investors/306/charter.pdf',                768000, 'application/pdf'),
    (19, 306, 9, 2025, 'kapitalinvest_eio_decision.pdf', 'investors/306/executive_decision.pdf',     256000, 'application/pdf'),
    -- Белов (ФЛ, 307)
    (20, 307, 1, 2025, 'belov_passport_main.pdf',        'investors/307/passport_main.pdf',         1024000, 'application/pdf'),
    (21, 307, 2, 2025, 'belov_passport_reg.pdf',         'investors/307/passport_registration.pdf',  512000, 'application/pdf'),
    -- Тихонова (ФЛ, 308)
    (22, 308, 1, 2025, 'tikhonova_passport_main.pdf',    'investors/308/passport_main.pdf',         1024000, 'application/pdf'),
    (23, 308, 2, 2025, 'tikhonova_passport_reg.pdf',     'investors/308/passport_registration.pdf',  512000, 'application/pdf'),
    -- ВенчурФонд (ЮЛ, 309)
    (24, 309, 6, 2025, 'venchurfond_egrul.pdf',          'investors/309/le_registration.pdf',        640000, 'application/pdf'),
    (25, 309, 8, 2025, 'venchurfond_charter.pdf',        'investors/309/charter.pdf',                896000, 'application/pdf'),
    (26, 309, 9, 2025, 'venchurfond_eio_decision.pdf',   'investors/309/executive_decision.pdf',     256000, 'application/pdf');

SELECT setval('investor_documents_id_seq', (SELECT MAX(id) FROM investor_documents));

--changeset investplatform:002-mock-proposal-documents
INSERT INTO proposal_documents (id, proposal_id, document_type_id, file_name, file_path, file_size, mime_type) VALUES
    -- Proposal 1 (ТехноИнвест акции)
    (1,  1, 1, 'technoinvest_fin_2025.pdf',           'proposals/1/financial_report.pdf',        2048576, 'application/pdf'),
    (2,  1, 3, 'technoinvest_issue_decision.pdf',      'proposals/1/issue_decision.pdf',          512000, 'application/pdf'),
    (3,  1, 6, 'technoinvest_draft_contract.pdf',      'proposals/1/draft_contract.pdf',         1024000, 'application/pdf'),
    (4,  1, 7, 'technoinvest_risk_warning.pdf',        'proposals/1/risk_warning.pdf',            384000, 'application/pdf'),
    -- Proposal 3 (ЭкоПак)
    (5,  3, 1, 'ecopak_fin_2025.pdf',                 'proposals/3/financial_report.pdf',         768000, 'application/pdf'),
    (6,  3, 6, 'ecopak_draft_contract.pdf',            'proposals/3/draft_contract.pdf',          640000, 'application/pdf'),
    (7,  3, 7, 'ecopak_risk_warning.pdf',              'proposals/3/risk_warning.pdf',            256000, 'application/pdf'),
    -- Proposal 5 (СтройИнновация акции)
    (8,  5, 1, 'stroinnovacia_fin_2025.pdf',           'proposals/5/financial_report.pdf',       3145728, 'application/pdf'),
    (9,  5, 2, 'stroinnovacia_audit_2025.pdf',         'proposals/5/audit_conclusion.pdf',       2097152, 'application/pdf'),
    (10, 5, 3, 'stroinnovacia_issue_decision.pdf',     'proposals/5/issue_decision.pdf',          640000, 'application/pdf'),
    (11, 5, 6, 'stroinnovacia_draft_contract.pdf',     'proposals/5/draft_contract.pdf',         1048576, 'application/pdf'),
    (12, 5, 7, 'stroinnovacia_risk_warning.pdf',       'proposals/5/risk_warning.pdf',            384000, 'application/pdf'),
    -- Proposal 7 (АгроТех акции)
    (13, 7, 1, 'agrotech_fin_2025.pdf',               'proposals/7/financial_report.pdf',        1048576, 'application/pdf'),
    (14, 7, 3, 'agrotech_issue_decision.pdf',          'proposals/7/issue_decision.pdf',          512000, 'application/pdf'),
    (15, 7, 6, 'agrotech_draft_contract.pdf',          'proposals/7/draft_contract.pdf',          768000, 'application/pdf'),
    (16, 7, 7, 'agrotech_risk_warning.pdf',            'proposals/7/risk_warning.pdf',            256000, 'application/pdf'),
    -- Proposal 9 (МедТехника обыкн.)
    (17, 9, 1, 'medtechnika_fin_2025.pdf',            'proposals/9/financial_report.pdf',        2621440, 'application/pdf'),
    (18, 9, 2, 'medtechnika_audit_2025.pdf',          'proposals/9/audit_conclusion.pdf',        1572864, 'application/pdf'),
    (19, 9, 3, 'medtechnika_issue_decision.pdf',      'proposals/9/issue_decision.pdf',           512000, 'application/pdf'),
    (20, 9, 6, 'medtechnika_draft_contract.pdf',      'proposals/9/draft_contract.pdf',          1024000, 'application/pdf'),
    (21, 9, 7, 'medtechnika_risk_warning.pdf',        'proposals/9/risk_warning.pdf',             384000, 'application/pdf'),
    -- Proposal 10 (МедТехника привил.)
    (22, 10, 1, 'medtechnika_priv_fin_2025.pdf',     'proposals/10/financial_report.pdf',       2621440, 'application/pdf'),
    (23, 10, 3, 'medtechnika_priv_issue_dec.pdf',    'proposals/10/issue_decision.pdf',          512000, 'application/pdf'),
    (24, 10, 6, 'medtechnika_priv_contract.pdf',     'proposals/10/draft_contract.pdf',         1024000, 'application/pdf'),
    (25, 10, 7, 'medtechnika_priv_risk.pdf',         'proposals/10/risk_warning.pdf',            384000, 'application/pdf');

SELECT setval('proposal_documents_id_seq', (SELECT MAX(id) FROM proposal_documents));

--changeset investplatform:002-mock-pd-consent-versions
INSERT INTO pd_consent_versions (id, consent_type_id, version_number, content, effective_from) VALUES
    (1, 1, 1, 'Я даю согласие на обработку моих персональных данных для создания учётной записи, идентификации и аутентификации на инвестиционной платформе в соответствии с ФЗ-152 «О персональных данных».', '2025-09-01'),
    (2, 2, 1, 'Я даю согласие на обработку моих персональных данных в рамках инвестиционной деятельности, включая заключение и исполнение договоров инвестирования, ведение реестра владельцев ценных бумаг.', '2025-09-01'),
    (3, 3, 1, 'Я даю согласие на передачу моих персональных данных в Банк России и иные уполномоченные органы в соответствии с ФЗ-259 «О привлечении инвестиций с использованием инвестиционных платформ».', '2025-09-01'),
    (4, 4, 1, 'Я даю согласие на получение информационных и рекламных материалов по электронной почте и SMS.', '2025-09-01'),
    (5, 5, 1, 'Я даю согласие на трансграничную передачу моих персональных данных в юрисдикции, обеспечивающие адекватную защиту прав субъектов персональных данных.', '2025-09-01');

SELECT setval('pd_consent_versions_id_seq', (SELECT MAX(id) FROM pd_consent_versions));

--changeset investplatform:002-mock-pd-consents
INSERT INTO pd_consents (user_id, consent_version_id, status, accepted_at, ip_address, user_agent) VALUES
    -- Investor 300 (Сидорова) — все обязательные + маркетинг
    (300, 1, 'ACTIVE', '2025-12-01 10:00:00', '192.168.1.100', 'Mozilla/5.0 Chrome/120'),
    (300, 2, 'ACTIVE', '2025-12-01 10:00:30', '192.168.1.100', 'Mozilla/5.0 Chrome/120'),
    (300, 3, 'ACTIVE', '2025-12-01 10:01:00', '192.168.1.100', 'Mozilla/5.0 Chrome/120'),
    (300, 4, 'ACTIVE', '2025-12-01 10:01:30', '192.168.1.100', 'Mozilla/5.0 Chrome/120'),
    -- Investor 301 (Волков) — только обязательные
    (301, 1, 'ACTIVE', '2026-01-15 14:30:00', '10.0.0.50',     'Mozilla/5.0 Firefox/119'),
    (301, 2, 'ACTIVE', '2026-01-15 14:30:30', '10.0.0.50',     'Mozilla/5.0 Firefox/119'),
    (301, 3, 'ACTIVE', '2026-01-15 14:31:00', '10.0.0.50',     'Mozilla/5.0 Firefox/119'),
    -- Investor 302 (АО ИнвестГрупп)
    (302, 1, 'ACTIVE', '2025-11-20 09:00:00', '172.16.0.10',   'Mozilla/5.0 Edge/120'),
    (302, 2, 'ACTIVE', '2025-11-20 09:00:30', '172.16.0.10',   'Mozilla/5.0 Edge/120'),
    (302, 3, 'ACTIVE', '2025-11-20 09:01:00', '172.16.0.10',   'Mozilla/5.0 Edge/120'),
    -- Emitent 200 (ТехноИнвест)
    (200, 1, 'ACTIVE', '2025-10-01 08:00:00', '192.168.0.10',  'Mozilla/5.0 Chrome/120'),
    (200, 2, 'ACTIVE', '2025-10-01 08:00:30', '192.168.0.10',  'Mozilla/5.0 Chrome/120'),
    (200, 3, 'ACTIVE', '2025-10-01 08:01:00', '192.168.0.10',  'Mozilla/5.0 Chrome/120'),
    -- Emitent 201 (Петров)
    (201, 1, 'ACTIVE', '2025-10-15 11:00:00', '192.168.0.20',  'Mozilla/5.0 Safari/17'),
    (201, 2, 'ACTIVE', '2025-10-15 11:00:30', '192.168.0.20',  'Mozilla/5.0 Safari/17'),
    (201, 3, 'ACTIVE', '2025-10-15 11:01:00', '192.168.0.20',  'Mozilla/5.0 Safari/17'),
    -- Investor 303 (Морозов) — обязательные + маркетинг
    (303, 1, 'ACTIVE', '2026-01-10 12:00:00', '85.140.10.55',  'Mozilla/5.0 Chrome/121'),
    (303, 2, 'ACTIVE', '2026-01-10 12:00:30', '85.140.10.55',  'Mozilla/5.0 Chrome/121'),
    (303, 3, 'ACTIVE', '2026-01-10 12:01:00', '85.140.10.55',  'Mozilla/5.0 Chrome/121'),
    (303, 4, 'ACTIVE', '2026-01-10 12:01:30', '85.140.10.55',  'Mozilla/5.0 Chrome/121'),
    -- Investor 304 (Николаева) — все обязательные
    (304, 1, 'ACTIVE', '2025-12-05 11:00:00', '178.20.30.40',  'Mozilla/5.0 Safari/17'),
    (304, 2, 'ACTIVE', '2025-12-05 11:00:30', '178.20.30.40',  'Mozilla/5.0 Safari/17'),
    (304, 3, 'ACTIVE', '2025-12-05 11:01:00', '178.20.30.40',  'Mozilla/5.0 Safari/17'),
    -- Investor 305 (Павлов) — обязательные
    (305, 1, 'ACTIVE', '2026-02-01 09:00:00', '95.31.10.20',   'Mozilla/5.0 Chrome/121'),
    (305, 2, 'ACTIVE', '2026-02-01 09:00:30', '95.31.10.20',   'Mozilla/5.0 Chrome/121'),
    (305, 3, 'ACTIVE', '2026-02-01 09:01:00', '95.31.10.20',   'Mozilla/5.0 Chrome/121'),
    -- Investor 306 (КапиталИнвест) — все + трансграничная
    (306, 1, 'ACTIVE', '2025-10-15 10:00:00', '212.45.67.89',  'Mozilla/5.0 Edge/120'),
    (306, 2, 'ACTIVE', '2025-10-15 10:00:30', '212.45.67.89',  'Mozilla/5.0 Edge/120'),
    (306, 3, 'ACTIVE', '2025-10-15 10:01:00', '212.45.67.89',  'Mozilla/5.0 Edge/120'),
    (306, 4, 'ACTIVE', '2025-10-15 10:01:30', '212.45.67.89',  'Mozilla/5.0 Edge/120'),
    (306, 5, 'ACTIVE', '2025-10-15 10:02:00', '212.45.67.89',  'Mozilla/5.0 Edge/120'),
    -- Investor 307 (Белов) — обязательные
    (307, 1, 'ACTIVE', '2026-02-20 16:00:00', '37.110.25.30',  'Mozilla/5.0 Chrome/121'),
    (307, 2, 'ACTIVE', '2026-02-20 16:00:30', '37.110.25.30',  'Mozilla/5.0 Chrome/121'),
    (307, 3, 'ACTIVE', '2026-02-20 16:01:00', '37.110.25.30',  'Mozilla/5.0 Chrome/121'),
    -- Investor 308 (Тихонова) — только регистрация (остальные не приняты)
    (308, 1, 'ACTIVE', '2026-03-01 10:00:00', '46.180.55.10',  'Mozilla/5.0 Firefox/122'),
    -- Investor 309 (ВенчурФонд) — все + трансграничная
    (309, 1, 'ACTIVE', '2025-09-01 08:00:00', '195.16.80.90',  'Mozilla/5.0 Chrome/119'),
    (309, 2, 'ACTIVE', '2025-09-01 08:00:30', '195.16.80.90',  'Mozilla/5.0 Chrome/119'),
    (309, 3, 'ACTIVE', '2025-09-01 08:01:00', '195.16.80.90',  'Mozilla/5.0 Chrome/119'),
    (309, 5, 'ACTIVE', '2025-09-01 08:01:30', '195.16.80.90',  'Mozilla/5.0 Chrome/119'),
    -- Emitent 202 (СтройИнновация)
    (202, 1, 'ACTIVE', '2025-10-20 09:00:00', '188.130.40.50', 'Mozilla/5.0 Chrome/120'),
    (202, 2, 'ACTIVE', '2025-10-20 09:00:30', '188.130.40.50', 'Mozilla/5.0 Chrome/120'),
    (202, 3, 'ACTIVE', '2025-10-20 09:01:00', '188.130.40.50', 'Mozilla/5.0 Chrome/120'),
    -- Emitent 203 (АгроТех)
    (203, 1, 'ACTIVE', '2025-11-01 10:00:00', '92.50.160.70',  'Mozilla/5.0 Firefox/119'),
    (203, 2, 'ACTIVE', '2025-11-01 10:00:30', '92.50.160.70',  'Mozilla/5.0 Firefox/119'),
    (203, 3, 'ACTIVE', '2025-11-01 10:01:00', '92.50.160.70',  'Mozilla/5.0 Firefox/119'),
    -- Emitent 204 (ИП Кузнецова)
    (204, 1, 'ACTIVE', '2025-12-01 14:00:00', '79.140.20.30',  'Mozilla/5.0 Safari/17'),
    (204, 2, 'ACTIVE', '2025-12-01 14:00:30', '79.140.20.30',  'Mozilla/5.0 Safari/17'),
    (204, 3, 'ACTIVE', '2025-12-01 14:01:00', '79.140.20.30',  'Mozilla/5.0 Safari/17'),
    -- Emitent 205 (МедТехника)
    (205, 1, 'ACTIVE', '2025-09-15 08:00:00', '213.87.130.40', 'Mozilla/5.0 Chrome/119'),
    (205, 2, 'ACTIVE', '2025-09-15 08:00:30', '213.87.130.40', 'Mozilla/5.0 Chrome/119'),
    (205, 3, 'ACTIVE', '2025-09-15 08:01:00', '213.87.130.40', 'Mozilla/5.0 Chrome/119');

--changeset investplatform:002-mock-registry-operations
INSERT INTO registry_operations (id, operation_type_id, operation_name, operation_kind, processing_datetime, processing_reference, date_state, account_transfer_id, account_receive_id, security_id, quantity, settlement_currency, settlement_amount, content) VALUES
    -- Placement of TINV-001 shares (emitent 200)
    (1,  8, 'Размещение акций TINV-001 (ООО ТехноИнвест)', 'EMISSION',
     '2025-08-20 10:00:00', 'REG-2025-0001', '2025-08-20', NULL, 200, 1, 25000.0000, 'RUB', 2500000.00,
     'Первичное размещение обыкновенных именных акций по решению о выпуске.'),
    -- Transfer TINV-001 to investor 300 (Сидорова)
    (2,  1, 'Переход прав на акции TINV-001 к инвестору Сидорова Е.А.', 'TRANSACTION',
     '2026-02-16 12:00:00', 'REG-2026-0001', '2026-02-16', 200, 300, 1, 500.0000, 'RUB', 50000.00,
     'Перевод акций по договору инвестирования DI-2026-000001.'),
    -- Transfer TINV-001 to investor 302 (АО ИнвестГрупп)
    (3,  1, 'Переход прав на акции TINV-001 к АО "ИнвестГрупп"', 'TRANSACTION',
     '2026-02-21 11:00:00', 'REG-2026-0002', '2026-02-21', 200, 302, 1, 2000.0000, 'RUB', 200000.00,
     'Перевод акций по договору инвестирования DI-2026-000002.'),
    -- Placement of TINV-002 bonds (emitent 200)
    (4,  8, 'Размещение облигаций TINV-002 (ООО ТехноИнвест)', 'EMISSION',
     '2025-11-10 10:00:00', 'REG-2025-0002', '2025-11-10', NULL, 200, 2, 10000.0000, 'RUB', 10000000.00,
     'Размещение купонных облигаций.'),
    -- Placement of PETR-001 shares (emitent 201)
    (5,  8, 'Размещение акций PETR-001 (ИП Петров А.Н.)', 'EMISSION',
     '2025-12-15 10:00:00', 'REG-2025-0003', '2025-12-15', NULL, 201, 3, 5000.0000, 'RUB', 2500000.00,
     'Первичное размещение обыкновенных именных акций.'),
    -- Placement of STRN-001 shares (emitent 202)
    (6,  8, 'Размещение акций STRN-001 (АО СтройИнновация)', 'EMISSION',
     '2025-10-01 10:00:00', 'REG-2025-0004', '2025-10-01', NULL, 202, 4, 15000.0000, 'RUB', 3000000.00,
     'Первичное размещение обыкновенных именных акций.'),
    -- Placement of AGRT-001 shares (emitent 203)
    (7,  8, 'Размещение акций AGRT-001 (ООО АгроТех)', 'EMISSION',
     '2025-11-20 10:00:00', 'REG-2025-0005', '2025-11-20', NULL, 203, 6, 20000.0000, 'RUB', 3000000.00,
     'Первичное размещение обыкновенных именных акций.'),
    -- Placement of MEDT-001 shares (emitent 205)
    (8,  8, 'Размещение акций MEDT-001 (ООО МедТехника)', 'EMISSION',
     '2025-12-01 10:00:00', 'REG-2025-0006', '2025-12-01', NULL, 205, 8, 10000.0000, 'RUB', 5000000.00,
     'Первичное размещение обыкновенных именных акций.'),
    -- Placement of MEDT-002 preferred shares (emitent 205)
    (9,  8, 'Размещение привилегированных акций MEDT-002 (ООО МедТехника)', 'EMISSION',
     '2025-12-01 11:00:00', 'REG-2025-0007', '2025-12-01', NULL, 205, 9, 10000.0000, 'RUB', 10000000.00,
     'Размещение привилегированных именных акций.'),
    -- Transfer TINV-001 to investor 303 (Морозов)
    (10, 1, 'Переход прав на акции TINV-001 к Морозову А.В.', 'TRANSACTION',
     '2026-02-27 10:00:00', 'REG-2026-0003', '2026-02-27', 200, 303, 1, 1000.0000, 'RUB', 100000.00,
     'Перевод акций по договору инвестирования DI-2026-000003.'),
    -- Transfer PETR-001 to investor 304 (Николаева)
    (11, 1, 'Переход прав на акции PETR-001 к Николаевой Д.С.', 'TRANSACTION',
     '2026-03-12 10:00:00', 'REG-2026-0004', '2026-03-12', 201, 304, 3, 50.0000, 'RUB', 25000.00,
     'Перевод акций по договору инвестирования DI-2026-000006.'),
    -- Transfer STRN-001 to investor 303 (Морозов)
    (12, 1, 'Переход прав на акции STRN-001 к Морозову А.В.', 'TRANSACTION',
     '2026-03-13 10:00:00', 'REG-2026-0005', '2026-03-13', 202, 303, 4, 1000.0000, 'RUB', 200000.00,
     'Перевод акций по договору инвестирования DI-2026-000008.'),
    -- Transfer STRN-001 to investor 304 (Николаева)
    (13, 1, 'Переход прав на акции STRN-001 к Николаевой Д.С.', 'TRANSACTION',
     '2026-03-17 10:00:00', 'REG-2026-0006', '2026-03-17', 202, 304, 4, 500.0000, 'RUB', 100000.00,
     'Перевод акций по договору инвестирования DI-2026-000009.'),
    -- Transfer AGRT-001 to investor 302 (АО ИнвестГрупп)
    (14, 1, 'Переход прав на акции AGRT-001 к АО "ИнвестГрупп"', 'TRANSACTION',
     '2026-03-04 10:00:00', 'REG-2026-0007', '2026-03-04', 203, 302, 6, 2000.0000, 'RUB', 300000.00,
     'Перевод акций по договору инвестирования DI-2026-000011.'),
    -- Transfer AGRT-001 to investor 305 (Павлов)
    (15, 1, 'Переход прав на акции AGRT-001 к ИП Павлову К.А.', 'TRANSACTION',
     '2026-03-07 10:00:00', 'REG-2026-0008', '2026-03-07', 203, 305, 6, 500.0000, 'RUB', 75000.00,
     'Перевод акций по договору инвестирования DI-2026-000012.'),
    -- Transfer MEDT-001 to investor 303 (Морозов)
    (16, 1, 'Переход прав на акции MEDT-001 к Морозову А.В.', 'TRANSACTION',
     '2026-03-23 10:00:00', 'REG-2026-0009', '2026-03-23', 205, 303, 8, 1000.0000, 'RUB', 500000.00,
     'Перевод акций по договору инвестирования DI-2026-000014.'),
    -- Transfer MEDT-002 to investor 304 (Николаева) — completed proposal 10
    (17, 1, 'Переход прав на привилегированные акции MEDT-002 к Николаевой Д.С.', 'TRANSACTION',
     '2025-10-13 10:00:00', 'REG-2025-0008', '2025-10-13', 205, 304, 9, 500.0000, 'RUB', 500000.00,
     'Перевод акций по договору инвестирования DI-2025-000016.'),
    -- Transfer MEDT-002 to investor 309 (ВенчурФонд) — completed proposal 10
    (18, 1, 'Переход прав на привилегированные акции MEDT-002 к АО "ВенчурФонд"', 'TRANSACTION',
     '2025-10-18 10:00:00', 'REG-2025-0009', '2025-10-18', 205, 309, 9, 1500.0000, 'RUB', 1500000.00,
     'Перевод акций по договору инвестирования DI-2025-000017.');

SELECT setval('registry_operations_id_seq', (SELECT MAX(id) FROM registry_operations));

--changeset investplatform:002-mock-registry-operation-documents
INSERT INTO registry_operation_documents (id, registry_operation_id, in_doc_num, in_reg_date, out_doc_num, out_doc_date) VALUES
    (1,  1,  'ВХ-2025-001', '2025-08-19 09:00:00', 'ИСХ-2025-001', '2025-08-20 10:00:00'),
    (2,  2,  'ВХ-2026-001', '2026-02-15 10:00:00', 'ИСХ-2026-001', '2026-02-16 12:00:00'),
    (3,  3,  'ВХ-2026-002', '2026-02-20 15:00:00', 'ИСХ-2026-002', '2026-02-21 11:00:00'),
    (4,  4,  'ВХ-2025-002', '2025-11-09 09:00:00', 'ИСХ-2025-002', '2025-11-10 10:00:00'),
    (5,  5,  'ВХ-2025-003', '2025-12-14 09:00:00', 'ИСХ-2025-003', '2025-12-15 10:00:00'),
    (6,  6,  'ВХ-2025-004', '2025-09-30 09:00:00', 'ИСХ-2025-004', '2025-10-01 10:00:00'),
    (7,  7,  'ВХ-2025-005', '2025-11-19 09:00:00', 'ИСХ-2025-005', '2025-11-20 10:00:00'),
    (8,  8,  'ВХ-2025-006', '2025-11-30 09:00:00', 'ИСХ-2025-006', '2025-12-01 10:00:00'),
    (9,  9,  'ВХ-2025-007', '2025-11-30 10:00:00', 'ИСХ-2025-007', '2025-12-01 11:00:00'),
    (10, 10, 'ВХ-2026-003', '2026-02-26 10:00:00', 'ИСХ-2026-003', '2026-02-27 10:00:00'),
    (11, 11, 'ВХ-2026-004', '2026-03-11 10:00:00', 'ИСХ-2026-004', '2026-03-12 10:00:00'),
    (12, 12, 'ВХ-2026-005', '2026-03-12 10:00:00', 'ИСХ-2026-005', '2026-03-13 10:00:00'),
    (13, 13, 'ВХ-2026-006', '2026-03-16 10:00:00', 'ИСХ-2026-006', '2026-03-17 10:00:00'),
    (14, 14, 'ВХ-2026-007', '2026-03-03 10:00:00', 'ИСХ-2026-007', '2026-03-04 10:00:00'),
    (15, 15, 'ВХ-2026-008', '2026-03-06 10:00:00', 'ИСХ-2026-008', '2026-03-07 10:00:00'),
    (16, 16, 'ВХ-2026-009', '2026-03-22 10:00:00', 'ИСХ-2026-009', '2026-03-23 10:00:00'),
    (17, 17, 'ВХ-2025-008', '2025-10-12 10:00:00', 'ИСХ-2025-008', '2025-10-13 10:00:00'),
    (18, 18, 'ВХ-2025-009', '2025-10-17 10:00:00', 'ИСХ-2025-009', '2025-10-18 10:00:00');

SELECT setval('registry_operation_documents_id_seq', (SELECT MAX(id) FROM registry_operation_documents));

--changeset investplatform:002-mock-notifications
INSERT INTO notifications (user_id, event_type_id, channel_id, entity_type, entity_id, title, body, delivery_status, is_read, read_at, sent_at, delivered_at) VALUES
    -- Emitent 200: proposal approved
    (200, 2, 1, 'investment_proposal', 1, 'ИП одобрено', 'Ваше инвестиционное предложение "Размещение акций ООО ТехноИнвест — серия А" одобрено оператором и будет опубликовано.', 'DELIVERED', TRUE, '2026-01-25 15:00:00', '2026-01-25 14:00:00', '2026-01-25 14:00:05'),
    -- Emitent 200: proposal rejected
    (200, 3, 1, 'investment_proposal', 4, 'ИП отклонено', 'Ваше инвестиционное предложение "Пилотный проект IoT-мониторинга" отклонено. Причина: недостаточно проработан бизнес-план.', 'DELIVERED', TRUE, '2025-11-10 16:00:00', '2025-11-10 15:00:00', '2025-11-10 15:00:05'),
    -- Investor 300: contract approved
    (300, 8, 1, 'investment_contract', 1, 'ДИ одобрен', 'Ваш договор инвестирования DI-2026-000001 одобрен оператором.', 'DELIVERED', TRUE, '2026-02-16 12:00:00', '2026-02-16 11:00:00', '2026-02-16 11:00:05'),
    -- Investor 300: payment received
    (300, 13, 1, 'payment', 1, 'Платёж получен', 'На ваш счёт зачислено 300 000,00 руб.', 'DELIVERED', TRUE, '2026-01-01 11:00:00', '2026-01-01 10:15:00', '2026-01-01 10:15:05'),
    -- Investor 302: contract approved
    (302, 8, 1, 'investment_contract', 2, 'ДИ одобрен', 'Ваш договор инвестирования DI-2026-000002 одобрен оператором.', 'DELIVERED', FALSE, NULL, '2026-02-21 10:00:00', '2026-02-21 10:00:05'),
    -- Emitent 201: proposal activated
    (201, 4, 1, 'investment_proposal', 3, 'ИП опубликовано', 'Ваше инвестиционное предложение "Размещение акций ЭкоПак" опубликовано и доступно инвесторам.', 'DELIVERED', TRUE, '2026-03-01 01:00:00', '2026-03-01 00:00:00', '2026-03-01 00:00:05'),
    -- Emitent 202: proposal 5 activated
    (202, 4, 1, 'investment_proposal', 5, 'ИП опубликовано', 'Ваше инвестиционное предложение "Размещение акций АО СтройИнновация — раунд B" опубликовано.', 'DELIVERED', TRUE, '2026-03-01 01:00:00', '2026-03-01 00:00:00', '2026-03-01 00:00:05'),
    -- Emitent 202: proposal 12 failed
    (202, 6, 1, 'investment_proposal', 12, 'ИП не состоялось', 'Размещение "Элитный ЖК Панорама — первый транш" не состоялось: недостаточный объём привлечённых средств.', 'DELIVERED', TRUE, '2026-03-01 10:00:00', '2026-02-28 23:59:59', '2026-03-01 00:00:05'),
    -- Emitent 203: proposal 7 activated
    (203, 4, 1, 'investment_proposal', 7, 'ИП опубликовано', 'Ваше инвестиционное предложение "Размещение акций ООО АгроТех — раунд Seed" опубликовано.', 'DELIVERED', TRUE, '2026-02-15 01:00:00', '2026-02-15 00:00:00', '2026-02-15 00:00:05'),
    -- Emitent 205: proposal 9 activated
    (205, 4, 1, 'investment_proposal', 9, 'ИП опубликовано', 'Ваше предложение "Размещение акций ООО МедТехника — серия А" опубликовано.', 'DELIVERED', TRUE, '2026-03-15 01:00:00', '2026-03-15 00:00:00', '2026-03-15 00:00:05'),
    -- Emitent 205: proposal 10 completed
    (205, 5, 1, 'investment_proposal', 10, 'ИП состоялось', 'Размещение привилегированных акций ООО "МедТехника" успешно завершено. Собрано 10 000 000 руб.', 'DELIVERED', TRUE, '2026-02-01 10:00:00', '2026-01-31 23:59:59', '2026-02-01 00:00:05'),
    -- Investor 303: contract 3 approved (proposal 1)
    (303, 8, 1, 'investment_contract', 3, 'ДИ одобрен', 'Ваш договор DI-2026-000003 одобрен оператором.', 'DELIVERED', TRUE, '2026-02-26 10:00:00', '2026-02-26 09:00:00', '2026-02-26 09:00:05'),
    -- Investor 303: contract 8 approved (proposal 5)
    (303, 8, 1, 'investment_contract', 8, 'ДИ одобрен', 'Ваш договор DI-2026-000008 одобрен оператором.', 'DELIVERED', FALSE, NULL, '2026-03-12 11:00:00', '2026-03-12 11:00:05'),
    -- Investor 303: contract 14 approved (proposal 9)
    (303, 8, 1, 'investment_contract', 14, 'ДИ одобрен', 'Ваш договор DI-2026-000014 одобрен оператором.', 'DELIVERED', FALSE, NULL, '2026-03-22 11:00:00', '2026-03-22 11:00:05'),
    -- Investor 303: payment received
    (303, 13, 1, 'payment', 7, 'Платёж получен', 'На ваш счёт зачислено 500 000,00 руб.', 'DELIVERED', TRUE, '2026-01-15 12:00:00', '2026-01-15 11:00:00', '2026-01-15 11:00:05'),
    -- Investor 304: contract 6 approved (proposal 3)
    (304, 8, 1, 'investment_contract', 6, 'ДИ одобрен', 'Ваш договор DI-2026-000006 одобрен оператором.', 'DELIVERED', TRUE, '2026-03-12 09:00:00', '2026-03-11 14:00:00', '2026-03-11 14:00:05'),
    -- Investor 304: contract 16 completed (proposal 10)
    (304, 11, 1, 'investment_contract', 16, 'ДИ состоялся', 'Договор DI-2025-000016 исполнен. Акции зачислены на ваш счёт.', 'DELIVERED', TRUE, '2026-02-01 11:00:00', '2026-01-31 23:59:59', '2026-02-01 00:00:05'),
    -- Investor 305: contract 12 approved (proposal 7)
    (305, 8, 1, 'investment_contract', 12, 'ДИ одобрен', 'Ваш договор DI-2026-000012 одобрен оператором.', 'DELIVERED', TRUE, '2026-03-06 15:00:00', '2026-03-06 14:00:00', '2026-03-06 14:00:05'),
    -- Investor 306: contract 15 rejected (proposal 9)
    (306, 9, 1, 'investment_contract', 15, 'ДИ отклонён', 'Ваш договор DI-2026-000015 отклонён. Причина: превышен лимит инвестирования.', 'DELIVERED', FALSE, NULL, '2026-03-23 15:00:00', '2026-03-23 15:00:05'),
    -- Investor 306: payment received
    (306, 13, 1, 'payment', 12, 'Платёж получен', 'На ваш счёт зачислено 3 000 000,00 руб.', 'DELIVERED', TRUE, '2025-11-01 09:00:00', '2025-11-01 08:30:00', '2025-11-01 08:30:05'),
    -- Investor 307: contract 7 withdrawn
    (307, 10, 1, 'investment_contract', 7, 'ДИ отозван', 'Ваш договор DI-2026-000007 отозван по вашему запросу.', 'DELIVERED', TRUE, '2026-03-15 17:00:00', '2026-03-15 16:00:00', '2026-03-15 16:00:05'),
    -- Investor 307: contract 13 failed
    (307, 12, 1, 'investment_contract', 13, 'ДИ не состоялся', 'Договор DI-2026-000013 признан несостоявшимся: оплата не поступила в установленный срок.', 'DELIVERED', FALSE, NULL, '2026-04-01 00:00:00', '2026-04-01 00:00:05'),
    -- Investor 309: contract 17 completed (proposal 10)
    (309, 11, 1, 'investment_contract', 17, 'ДИ состоялся', 'Договор DI-2025-000017 исполнен. Акции зачислены на ваш счёт.', 'DELIVERED', TRUE, '2026-02-01 11:00:00', '2026-01-31 23:59:59', '2026-02-01 00:00:05'),
    -- Investor 309: payment received
    (309, 13, 1, 'payment', 16, 'Платёж получен', 'На ваш счёт зачислено 3 000 000,00 руб.', 'DELIVERED', TRUE, '2025-10-01 10:00:00', '2025-10-01 09:00:00', '2025-10-01 09:00:05'),
    -- Pending notification (not yet delivered)
    (308, 21, 1, 'investor', 308, 'Требуется верификация', 'Для начала инвестирования необходимо завершить верификацию: загрузите документы и примите декларацию о рисках.', 'PENDING', FALSE, NULL, NULL, NULL),
    -- Failed notification
    (307, 13, 2, 'payment', 14, 'Платёж получен', 'На ваш счёт зачислено 150 000,00 руб.', 'FAILED', FALSE, NULL, '2026-02-25 12:00:00', NULL);

--changeset investplatform:002-mock-auth-audit-log
INSERT INTO auth_audit_log (user_id, event_type, ip_address, user_agent, is_successful) VALUES
    -- Operators
    (100, 'LOGIN',  '192.168.1.1',   'Mozilla/5.0 Chrome/120', TRUE),
    (101, 'LOGIN',  '192.168.1.2',   'Mozilla/5.0 Chrome/120', TRUE),
    (102, 'LOGIN',  '192.168.1.3',   'Mozilla/5.0 Chrome/121', TRUE),
    (103, 'LOGIN',  '192.168.1.4',   'Mozilla/5.0 Firefox/120', TRUE),
    (104, 'LOGIN',  '192.168.1.5',   'Mozilla/5.0 Chrome/121', TRUE),
    -- Emitents
    (200, 'LOGIN',  '192.168.0.10',  'Mozilla/5.0 Chrome/120', TRUE),
    (200, 'LOGOUT', '192.168.0.10',  'Mozilla/5.0 Chrome/120', TRUE),
    (200, 'LOGIN',  '192.168.0.10',  'Mozilla/5.0 Chrome/121', TRUE),
    (201, 'LOGIN',  '192.168.0.20',  'Mozilla/5.0 Safari/17',  TRUE),
    (202, 'LOGIN',  '188.130.40.50', 'Mozilla/5.0 Chrome/120', TRUE),
    (202, 'LOGIN',  '188.130.40.50', 'Mozilla/5.0 Chrome/121', TRUE),
    (203, 'LOGIN',  '92.50.160.70',  'Mozilla/5.0 Firefox/119', TRUE),
    (204, 'LOGIN',  '79.140.20.30',  'Mozilla/5.0 Safari/17',  TRUE),
    (205, 'LOGIN',  '213.87.130.40', 'Mozilla/5.0 Chrome/119', TRUE),
    (205, 'LOGIN',  '213.87.130.40', 'Mozilla/5.0 Chrome/121', TRUE),
    -- Investors
    (300, 'LOGIN',  '192.168.1.100', 'Mozilla/5.0 Chrome/120', TRUE),
    (300, 'LOGIN',  '10.0.0.99',     'Mozilla/5.0 Chrome/120', FALSE),
    (300, 'LOGIN',  '192.168.1.100', 'Mozilla/5.0 Chrome/121', TRUE),
    (301, 'LOGIN',  '10.0.0.50',     'Mozilla/5.0 Firefox/119', TRUE),
    (302, 'LOGIN',  '172.16.0.10',   'Mozilla/5.0 Edge/120',   TRUE),
    (302, 'LOGOUT', '172.16.0.10',   'Mozilla/5.0 Edge/120',   TRUE),
    (303, 'LOGIN',  '85.140.10.55',  'Mozilla/5.0 Chrome/121', TRUE),
    (303, 'LOGIN',  '85.140.10.55',  'Mozilla/5.0 Chrome/121', TRUE),
    (303, 'TOKEN_REFRESH', '85.140.10.55', 'Mozilla/5.0 Chrome/121', TRUE),
    (304, 'LOGIN',  '178.20.30.40',  'Mozilla/5.0 Safari/17',  TRUE),
    (304, 'LOGIN',  '178.20.30.40',  'Mozilla/5.0 Safari/17',  TRUE),
    (305, 'LOGIN',  '95.31.10.20',   'Mozilla/5.0 Chrome/121', TRUE),
    (306, 'LOGIN',  '212.45.67.89',  'Mozilla/5.0 Edge/120',   TRUE),
    (306, 'LOGIN',  '212.45.67.89',  'Mozilla/5.0 Edge/121',   TRUE),
    (307, 'LOGIN',  '37.110.25.30',  'Mozilla/5.0 Chrome/121', TRUE),
    (307, 'LOGIN',  '37.110.25.30',  'Mozilla/5.0 Chrome/121', FALSE),
    (307, 'LOGIN',  '37.110.25.30',  'Mozilla/5.0 Chrome/121', TRUE),
    (308, 'LOGIN',  '46.180.55.10',  'Mozilla/5.0 Firefox/122', TRUE),
    (309, 'LOGIN',  '195.16.80.90',  'Mozilla/5.0 Chrome/119', TRUE),
    (309, 'LOGIN',  '195.16.80.90',  'Mozilla/5.0 Chrome/121', TRUE),
    (309, 'LOGOUT', '195.16.80.90',  'Mozilla/5.0 Chrome/121', TRUE),
    (309, 'TOKEN_REFRESH', '195.16.80.90', 'Mozilla/5.0 Chrome/121', TRUE);

--changeset investplatform:002-mock-notification-preferences
INSERT INTO notification_preferences (user_id, event_type_id, channel_id, is_enabled) VALUES
    -- Investor 300 (Сидорова) — web + email for contracts, web only for payments
    (300, 7, 1, TRUE), (300, 7, 2, TRUE),   -- contract_created: web + email
    (300, 8, 1, TRUE), (300, 8, 2, TRUE),   -- contract_approved: web + email
    (300, 13, 1, TRUE), (300, 13, 2, FALSE), -- payment_received: web only
    -- Investor 303 (Морозов) — all channels for everything
    (303, 7, 1, TRUE), (303, 7, 2, TRUE), (303, 7, 4, TRUE),
    (303, 8, 1, TRUE), (303, 8, 2, TRUE), (303, 8, 4, TRUE),
    (303, 13, 1, TRUE), (303, 13, 2, TRUE), (303, 13, 4, TRUE),
    -- Investor 304 (Николаева) — web only
    (304, 7, 1, TRUE), (304, 8, 1, TRUE), (304, 13, 1, TRUE),
    -- Investor 309 (ВенчурФонд) — web + email for all
    (309, 7, 1, TRUE), (309, 7, 2, TRUE),
    (309, 8, 1, TRUE), (309, 8, 2, TRUE),
    (309, 11, 1, TRUE), (309, 11, 2, TRUE),
    (309, 13, 1, TRUE), (309, 13, 2, TRUE),
    -- Emitent 200 (ТехноИнвест) — web + email for proposals
    (200, 2, 1, TRUE), (200, 2, 2, TRUE),
    (200, 3, 1, TRUE), (200, 3, 2, TRUE),
    (200, 4, 1, TRUE), (200, 4, 2, TRUE),
    -- Emitent 205 (МедТехника) — web + email + push for proposals
    (205, 2, 1, TRUE), (205, 2, 2, TRUE), (205, 2, 4, TRUE),
    (205, 4, 1, TRUE), (205, 4, 2, TRUE), (205, 4, 4, TRUE),
    (205, 5, 1, TRUE), (205, 5, 2, TRUE), (205, 5, 4, TRUE);
