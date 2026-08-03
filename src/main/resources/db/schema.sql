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
    pincode             VARCHAR(20)     NOT NULL,
    latitude            NUMERIC(20, 6)  ,
    longitude           NUMERIC(20, 6)  ,
    is_default          SMALLINT        NOT NULL
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

-- Documents (KYC, Verification)CREATE TABLE fks_documents (
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
