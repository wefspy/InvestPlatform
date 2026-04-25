--liquibase formatted sql

--changeset investplatform:003-add-reserved-amount
ALTER TABLE investment_proposals
    ADD COLUMN reserved_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00
    CHECK (reserved_amount >= 0);

ALTER TABLE investment_proposals DROP CONSTRAINT chk_collected_le_max;

ALTER TABLE investment_proposals ADD CONSTRAINT chk_collected_plus_reserved_le_max
    CHECK (collected_amount + reserved_amount <= max_investment_amount);

--changeset investplatform:003-allow-system-status-history
ALTER TABLE proposal_status_history ALTER COLUMN changed_by DROP NOT NULL;
ALTER TABLE contract_status_history ALTER COLUMN changed_by DROP NOT NULL;
