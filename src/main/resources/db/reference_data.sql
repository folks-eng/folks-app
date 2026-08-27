-- Insert admin users.

INSERT INTO public.fks_users (external_id, full_name, email, phone1, phone2, password_hash, role, status, created_at, updated_at)
VALUES 
(RANDOM_UUID(), 'Folks Admin', 'folks.admin@javalabs.org', '9000000000', NULL, '7c6a180b36896a0a8c02787eeafb0e4c', 'ADMIN', 'ACTIVE', current_timestamp, null),
(RANDOM_UUID(), 'Node Admin', 'node.admin@javalabs.org', '8000000000', NULL, '6cb75f652a9b52798eb6cf2201057c73', 'ADMIN', 'ACTIVE', current_timestamp, null),
(RANDOM_UUID(), 'Support Admin', 'support.admin@javalabs.org', '7000000000', NULL, '6cb75f652a9b52798eb6cf2201057c73', 'ADMIN', 'ACTIVE', current_timestamp, null);
