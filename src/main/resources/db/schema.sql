-- Run the below script to generate the tables --
-- h2-script.sh -url jdbc:h2:tcp://localhost:9092/~/testdb -user test -password test123 -script /Users/schan280/temp/folks-app/src/main/resources/db/schema.sql --

-- Table Script --

-- 1. Users (Customers + Service Professionals + Admins)

CREATE TABLE fks_users (
    user_id             INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    external_id         VARCHAR(36)     NOT NULL,
    full_name           VARCHAR(96)     NOT NULL,
    email               VARCHAR(128)    NOT NULL,
    phone1              VARCHAR(20)     NOT NULL,
    phone2              VARCHAR(20)     ,
    password_hash       TEXT            ,
    role                VARCHAR(16)     NOT NULL CHECK (role IN ('CUSTOMER', 'PROFESSIONAL', 'ADMIN')),
    status              VARCHAR(16)     NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED')),
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP     
);

-- Address

CREATE TABLE fks_addresses (
    address_id          INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id             INT             NOT NULL,
    address_line1       VARCHAR(128)    NOT NULL,
    address_line2       VARCHAR(128)    ,
    city                VARCHAR(64)     NOT NULL,
    state               VARCHAR(64)     NOT NULL,
    pincode             INT             NOT NULL,
    latitude            NUMERIC(20, 6)  ,
    longitude           NUMERIC(20, 6)  ,
    is_default          SMALLINT        NOT NULL,
    label               VARCHAR(20)     NOT NULL,
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP     
);

-- 2. Service Catalog - Categories & Services

CREATE TABLE fks_categories (
    category_id         INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    name                VARCHAR(128)    NOT NULL,
    parent_id           INT        
);

CREATE TABLE fks_services (
    service_id          INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    category_id         INT             NOT NULL,
    name                VARCHAR(128)    NOT NULL,
    description         TEXT            ,
    base_price          NUMERIC(7, 2)   NOT NULL,
    duration_minutes    SMALLINT             
);

-- Professional Profiles

CREATE TABLE fks_professionals (
    professional_id     INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id             INT             NOT NULL,
    bio                 TEXT            ,
    experience_years    SMALLINT        NOT NULL,
    rating_avg          NUMERIC(3, 2)   ,
    is_verified         SMALLINT        NOT NULL
);

-- Professional Skills

CREATE TABLE fks_professional_services (
    id                  INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    professional_id     INT             NOT NULL,
    service_id          INT             NOT NULL,
    price               NUMERIC(7, 2)   NOT NULL,
    is_active           SMALLINT        NOT NULL
);

-- 3. Booking & Scheduling

-- Booking

CREATE TABLE fks_bookings (
    booking_id          VARCHAR(36)     NOT NULL,
    customer_id         INT             NOT NULL,
    professional_id     INT             NOT NULL,
    service_id          INT             NOT NULL,
    address_id          INT             NOT NULL,
    scheduled_at        TIMESTAMP       NOT NULL,
    status              VARCHAR(255)    CHECK (status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    total_amount        NUMERIC(20, 6)  NOT NULL,
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP       
);

-- Time Slots / Availability

CREATE TABLE fks_availability (
    availability_id     INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    professional_id     INT             NOT NULL,
    date                DATE            NOT NULL,
    start_time          TIMESTAMP       ,
    end_time            TIMESTAMP       ,
    is_booked           SMALLINT        NOT NULL
);

-- 4. Payments & Pricing

-- Payments

CREATE TABLE fks_payments (
    payment_id          INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    booking_id          VARCHAR(36)     NOT NULL,
    amount              NUMERIC(7, 2)   NOT NULL,
    payment_method      VARCHAR(16)     CHECK (payment_method IN ('CARD', 'UPI', 'WALLET', 'COD')),
    payment_status      VARCHAR(16)     CHECK (payment_status IN ('INITIATED', 'SUCCESS', 'FAILED', 'REFUNDED')),
    transaction_ref     VARCHAR(128)    NOT NULL,
    paid_at             TIMESTAMP       NOT NULL
);

-- Coupons & Discounts

CREATE TABLE fks_coupons (
    coupon_id           INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    code                VARCHAR(50)     NOT NULL,
    discount_type       VARCHAR(16)     NOT NULL,
    discount_value      NUMERIC(7, 2)   ,
    max_discount        NUMERIC(7, 2)   ,
    expiry_date         DATE            NOT NULL,
    usage_limit         SMALLINT
);

CREATE TABLE fks_coupon_usage (
    usage_id            INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    coupon_id           INT             NOT NULL,
    user_id             INT             NOT NULL,
    booking_id          VARCHAR(36)     NOT NULL,
    used_at             TIMESTAMP       NOT NULL
);

-- 5. Ratings & Reviews

CREATE TABLE fks_reviews (
    review_id           INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    booking_id          VARCHAR(36)     NOT NULL,
    customer_id         INT             NOT NULL,
    professional_id     INT             NOT NULL,
    rating              SMALLINT        ,
    comment             TEXT            ,
    created_at          TIMESTAMP       NOT NULL
);

-- 6. Communication

CREATE TABLE fks_conversations (
    conversation_id     INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    booking_id          VARCHAR(36)     NOT NULL,
    created_at          TIMESTAMP       NOT NULL
);

CREATE TABLE fks_messages (
    message_id          INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    conversation_id     INT             NOT NULL,
    sender_id           INT             NOT NULL,
    message_text        TEXT            ,
    sent_at             TIMESTAMP       NOT NULL
);

-- 7. Operations & Logistics

CREATE TABLE fks_job_status (
    log_id              INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    booking_id          VARCHAR(36)     NOT NULL,
    status              VARCHAR(32)     NOT NULL,
    updated_at          TIMESTAMP       ,
    updated_by          INT       
);

-- 8. Admin & Compliance

-- Documents (KYC, Verification)

CREATE TABLE fks_documents (
    document_id         INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id             INT             NOT NULL,
    document_type       VARCHAR(50)     NOT NULL,
    document_url        TEXT            ,
    verification_status VARCHAR(16)     CHECK (verification_status IN ('PENDING', 'APPROVED', 'REJECTED')),
    uploaded_at         TIMESTAMP     
);

-- 9. Surge Pricing

CREATE TABLE fks_pricing_rules (
    rule_id             INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    service_id          INT             NOT NULL,
    city                VARCHAR(100)    NOT NULL,
    multiplier          NUMERIC(7, 2)   NOT NULL,
    start_time          TIMESTAMP       NOT NULL,
    end_time            TIMESTAMP       NOT NULL
);

-- 10. Wallet Systems

CREATE TABLE fks_wallets (
    wallet_id           VARCHAR(36)     NOT NULL,
    user_id             INT             NOT NULL,
    balance             NUMERIC(7, 2)  
);

CREATE TABLE fks_wallet_transactions (
    txn_id              VARCHAR(64)     NOT NULL,
    wallet_id           VARCHAR(36)     NOT NULL,
    amount              NUMERIC(7, 2)   NOT NULL,
    type                VARCHAR(16)     CHECK (type IN ('CREDIT', 'DEBIT')),
    created_at          TIMESTAMP       NOT NULL
);

-- 11. Analytics / Audit

CREATE TABLE fks_audit_logs (
    log_id              INT             GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id             INT             NOT NULL,
    action              VARCHAR(64)     NOT NULL,
    entity_type         VARCHAR(50)     NOT NULL,
    entity_id           INT             NOT NULL,
    created_at          TIMESTAMP       NOT NULL
);


-- Primary Key Constraint --

ALTER TABLE fks_payments
ADD CONSTRAINT fks_payments_pk
PRIMARY KEY (payment_id);

ALTER TABLE fks_users
ADD CONSTRAINT fks_users_pk
PRIMARY KEY (user_id);

ALTER TABLE fks_addresses
ADD CONSTRAINT fks_addresses_pk
PRIMARY KEY (address_id);

ALTER TABLE fks_categories
ADD CONSTRAINT fks_categories_pk
PRIMARY KEY (category_id);

ALTER TABLE fks_services
ADD CONSTRAINT fks_services_pk
PRIMARY KEY (service_id);

ALTER TABLE fks_professionals
ADD CONSTRAINT fks_professionals_pk
PRIMARY KEY (professional_id);

ALTER TABLE fks_professional_services
ADD CONSTRAINT fks_professional_services_pk
PRIMARY KEY (id);

ALTER TABLE fks_bookings
ADD CONSTRAINT fks_bookings_pk
PRIMARY KEY (booking_id);

ALTER TABLE fks_availability
ADD CONSTRAINT fks_availability_pk
PRIMARY KEY (availability_id);

ALTER TABLE fks_reviews
ADD CONSTRAINT fks_reviews_pk
PRIMARY KEY (review_id);

ALTER TABLE fks_conversations
ADD CONSTRAINT fks_conversations_pk
PRIMARY KEY (conversation_id);

ALTER TABLE fks_messages
ADD CONSTRAINT fks_messages_pk
PRIMARY KEY (message_id);

ALTER TABLE fks_job_status
ADD CONSTRAINT fks_job_status_pk
PRIMARY KEY (log_id);

ALTER TABLE fks_documents
ADD CONSTRAINT fks_documents_pk
PRIMARY KEY (document_id);

ALTER TABLE fks_audit_logs
ADD CONSTRAINT fks_audit_logs_pk
PRIMARY KEY (log_id);

ALTER TABLE fks_pricing_rules
ADD CONSTRAINT fks_pricing_rules_pk
PRIMARY KEY (rule_id);

ALTER TABLE fks_wallets
ADD CONSTRAINT fks_wallets_pk
PRIMARY KEY (wallet_id);

ALTER TABLE fks_wallet_transactions
ADD CONSTRAINT fks_wallet_transactions_pk
PRIMARY KEY (txn_id);

ALTER TABLE fks_coupons
ADD CONSTRAINT fks_coupon_pk
PRIMARY KEY (coupon_id);

ALTER TABLE fks_coupon_usage
ADD CONSTRAINT fks_coupon_usage_pk
PRIMARY KEY (usage_id);

-- Foreign Key Constraint --

ALTER TABLE fks_addresses
ADD CONSTRAINT fks_addresses_fk1
FOREIGN KEY (user_id)
REFERENCES fks_users (user_id);

ALTER TABLE fks_professionals
ADD CONSTRAINT fks_professionals_fk1
FOREIGN KEY (user_id)
REFERENCES fks_users (user_id);

ALTER TABLE fks_bookings
ADD CONSTRAINT fks_bookings_fk1
FOREIGN KEY (customer_id)
REFERENCES fks_users (user_id);

ALTER TABLE fks_coupon_usage
ADD CONSTRAINT fks_coupon_usage_fk1
FOREIGN KEY (user_id)
REFERENCES fks_users (user_id);

ALTER TABLE fks_bookings
ADD CONSTRAINT fks_bookings_fk2
FOREIGN KEY (address_id)
REFERENCES fks_addresses (address_id);

ALTER TABLE fks_availability
ADD CONSTRAINT fks_availability_fk1
FOREIGN KEY (professional_id)
REFERENCES fks_professionals (professional_id);

ALTER TABLE fks_professional_services
ADD CONSTRAINT fks_professional_services_fk1
FOREIGN KEY (professional_id)
REFERENCES fks_professionals (professional_id);

ALTER TABLE fks_professional_services
ADD CONSTRAINT fks_professional_services_fk2
FOREIGN KEY (service_id)
REFERENCES fks_services (service_id);

ALTER TABLE fks_categories
ADD CONSTRAINT fks_categories_fk1
FOREIGN KEY (parent_id)
REFERENCES fks_categories (category_id);

ALTER TABLE fks_services
ADD CONSTRAINT fks_services_fk1
FOREIGN KEY (category_id)
REFERENCES fks_categories (category_id);

ALTER TABLE fks_bookings
ADD CONSTRAINT fks_bookings_fk3
FOREIGN KEY (service_id)
REFERENCES fks_services (service_id);

ALTER TABLE fks_messages
ADD CONSTRAINT fks_messages_fk1
FOREIGN KEY (conversation_id)
REFERENCES fks_conversations (conversation_id);

ALTER TABLE fks_coupon_usage
ADD CONSTRAINT fks_coupon_usage_fk2
FOREIGN KEY (booking_id)
REFERENCES fks_bookings (booking_id);

ALTER TABLE fks_payments
ADD CONSTRAINT fks_payments_fk1
FOREIGN KEY (booking_id)
REFERENCES fks_bookings (booking_id);


-- Indexes --

CREATE UNIQUE INDEX fks_users_uk1
ON fks_users
USING BTREE (external_id);

CREATE UNIQUE INDEX fks_users_uk2
ON fks_users
USING BTREE (phone1);

