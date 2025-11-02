--liquibase formatted sql

--changeset system:003-modify-customers-company-nullable
--comment: Modify customers table to allow NULL company_id temporarily

-- Modify customers table to allow NULL company_id
ALTER TABLE customers ALTER COLUMN company_id DROP NOT NULL;
