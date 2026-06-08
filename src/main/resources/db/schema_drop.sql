-- Run the below script to drop all the tables --
-- h2-script.sh -url jdbc:h2:tcp://localhost:9092/~/testdb -user test -password test123 -script /Users/schan280/Projects/folks-app/src/main/resources/db/schema_drop.sql --

DROP TABLE information_schema.columns;
DROP TABLE fks_payments;
DROP TABLE fks_documents;
DROP TABLE fks_wallet_transactions;
DROP TABLE fks_job_status;
DROP TABLE fks_services;
DROP TABLE fks_availability;
DROP TABLE pan_info;
DROP TABLE fks_professionals;
DROP TABLE fks_bookings;
DROP TABLE fks_coupons;
DROP TABLE fks_wallets;
DROP TABLE fks_coupon_usage;
DROP TABLE information_schema.key_column_usage;
DROP TABLE information_schema.check_constraints;
DROP TABLE information_schema.tables;
DROP TABLE fks_conversations;
DROP TABLE fks_categories;
DROP TABLE fks_pricing_rules;
DROP TABLE fks_users;
DROP TABLE fks_reviews;
DROP TABLE fks_messages;
DROP TABLE fks_addresses;
DROP TABLE fks_audit_logs;
DROP TABLE fks_professional_services;
