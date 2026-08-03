-- Run the below script to generate the tables --
-- h2-script.sh -url jdbc:h2:tcp://localhost:9092/~/testdb -user test -password test123 -script /Users/schan280/temp/folks-app/src/main/resources/db/schema.sql --

-- Table Script --

DROP TABLE IF EXISTS fks_addresses CASCADE;
DROP TABLE IF EXISTS fks_audit_logs CASCADE;
DROP TABLE IF EXISTS fks_availability CASCADE;
DROP TABLE IF EXISTS fks_bookings CASCADE;
DROP TABLE IF EXISTS fks_categories CASCADE;
DROP TABLE IF EXISTS fks_conversations CASCADE;
DROP TABLE IF EXISTS fks_coupon_usage CASCADE;
DROP TABLE IF EXISTS fks_coupons CASCADE;
DROP TABLE IF EXISTS fks_documents CASCADE;
DROP TABLE IF EXISTS fks_job_status CASCADE;
DROP TABLE IF EXISTS fks_messages  CASCADE;
DROP TABLE IF EXISTS fks_payments  CASCADE;
DROP TABLE IF EXISTS fks_pricing_rules  CASCADE;
DROP TABLE IF EXISTS fks_professional_services CASCADE;
DROP TABLE IF EXISTS fks_professionals  CASCADE;
DROP TABLE IF EXISTS fks_reviews CASCADE;
DROP TABLE IF EXISTS fks_services CASCADE;
DROP TABLE IF EXISTS fks_users CASCADE;
DROP TABLE IF EXISTS fks_wallet_transactions CASCADE;
DROP TABLE IF EXISTS fks_wallets CASCADE;
