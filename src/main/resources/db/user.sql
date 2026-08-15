INSERT INTO ecm_user_roles (ads_id, email, role, description, created_date, active, updated_date) VALUES ('ecmdaemon', 'ecm.daemon@aexp.com', 'admin', '', current_timestamp, 'Y', null);
INSERT INTO ecm_user_roles (ads_id, email, role, description, created_date, active, updated_date) VALUES ('ecmservice', 'ecm.service@aexp.com', 'admin', '', current_timestamp, 'Y', null);
INSERT INTO ecm_user_roles (ads_id, email, role, description, created_date, active, updated_date) VALUES ('ecmadmin', 'ecm.admin@aexp.com', 'admin', '', current_timestamp, 'Y', null);
INSERT INTO ecm_user_roles (ads_id, email, role, description, created_date, active, updated_date) VALUES ('ecmuser', 'ecm.user@aexp.com', 'admin', '', current_timestamp, 'Y', null);

INSERT INTO ecm_locks (lock_id, acquired, owner, created_date, updated_date) VALUES ('daily.work.lock', 'N', 'None', current_timestamp, null);
INSERT INTO ecm_locks (lock_id, acquired, owner, created_date, updated_date) VALUES ('notif.archiver.lock', 'N', 'None', current_timestamp, null);

INSERT INTO ecm_metric_groups (mg_name, mg_desc, mg_type, store_table, store_class, created_date, updated_date) VALUES 
('inv_count', 'Capture the count metric for URI invocation', 'agg', 'ecm_agg_metrics', 'com.aexp.ea.ecm.entity.AggMetric', '2024-08-30 08:33:10.33', NULL),
('response_status', 'Capture the response status metric for URI invocation', 'agg', 'ecm_agg_metrics', 'com.aexp.ea.ecm.entity.AggMetric', '2024-08-30 08:33:10.331', NULL),
('response_time', 'Capture the response time metric for URI invocation', 'agg', 'ecm_agg_metrics', 'com.aexp.ea.ecm.entity.AggMetric', '2024-08-30 08:33:10.331', NULL),
('config_version', 'Capture the version change metric', 'agg', 'ecm_agg_metrics', 'com.aexp.ea.ecm.entity.AggMetric', '2024-08-30 08:33:10.331', NULL),
('memory_usage', 'Capture the memory usage of the host', 'raw', 'ecm_raw_metrics', 'com.aexp.ea.ecm.entity.RawMetric', '2024-08-30 08:33:10.331', NULL),
('gc_usage', 'Garbage collector usage of a jvm', 'raw', 'ecm_raw_metrics', 'com.aexp.ea.ecm.entity.RawMetric', '2024-08-30 08:33:10.331', NULL),
('cpu_usage', 'Capture the cpu usage of a jvm', 'raw', 'ecm_raw_metrics', 'com.aexp.ea.ecm.entity.RawMetric', '2024-08-30 08:33:10.331', NULL);

INSERT INTO ecm_metric_columns (mg_name, mc_name, mc_desc, mc_order, store_column, is_key, data_type, created_date, updated_date) VALUES 
('inv_count', 'server', 'Server Name', 1, 'ks1', 1, 'S', '2024-08-30 08:33:10.33', NULL),
('inv_count', 'iteration', 'Iteration count', 2, 'ks2', 1, 'S', '2024-08-30 08:33:10.33', NULL),
('inv_count', 'resource', 'Resource name or URI', 3, 'vs1', 0, 'S', '2024-08-30 08:33:10.33', NULL),
('inv_count', 'env', 'Server environment', 4, 'vs2', 0, 'S', '2024-08-30 08:33:10.33', NULL),
('inv_count', 'count', 'Number of time the target URI has been invoked', 5, 'vn1', 0, 'N', '2024-08-30 08:33:10.33', NULL),
('response_status', 'server', 'Server Name', 1, 'ks1', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('response_status', 'iteration', 'Iteration count', 2, 'ks2', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('response_status', 'resource', 'Resource name or URI', 3, 'vs1', 0, 'S', '2024-08-30 08:33:10.331', NULL),
('response_status', 'env', 'Server environment', 4, 'vs2', 0, 'S', '2024-08-30 08:33:10.331', NULL),
('response_status', 'status_200', 'Number of time the target URI has returned 200 status code', 5, 'vn1', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_status', 'status_204', 'Number of time the target URI has returned 204 status code', 6, 'vn2', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_status', 'status_400', 'Number of time the target URI has returned 400 status code', 7, 'vn3', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_status', 'status_401', 'Number of time the target URI has returned 401 status code', 8, 'vn4', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_status', 'status_404', 'Number of time the target URI has returned 404 status code', 9, 'vn5', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_status', 'status_500', 'Number of time the target URI has returned 500 status code', 10, 'vn6', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_status', 'status_503', 'Number of time the target URI has returned 503 status code', 11, 'vn7', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_status', 'status_unknown', 'Number of time the target URI has returned unknown status code', 12, 'vs3', 0, 'S', '2024-08-30 08:33:10.331', NULL),
('response_time', 'server', 'Server Name', 1, 'ks1', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('response_time', 'iteration', 'Iteration count', 2, 'ks2', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('response_time', 'resource', 'Resource name or URI', 3, 'vs1', 0, 'S', '2024-08-30 08:33:10.331', NULL),
('response_time', 'env', 'Server environment', 4, 'vs2', 0, 'S', '2024-08-30 08:33:10.331', NULL),
('response_time', 'min', 'Minimum response time', 5, 'vn1', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_time', 'max', 'Maximum response time', 6, 'vn2', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_time', 'avg', 'Average response time', 7, 'vn3', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_time', 'first_val', 'First Value within the time window', 8, 'vn4', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('response_time', 'last_val', 'Last Value within the time window', 9, 'vn5', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('config_version', 'server', 'Server Name', 1, 'ks1', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('config_version', 'iteration', 'Iteration count', 2, 'ks2', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('config_version', 'resource', 'Resource name or URI', 3, 'vs1', 0, 'S', '2024-08-30 08:33:10.331', NULL),
('config_version', 'env', 'Server environment', 4, 'vs2', 0, 'S', '2024-08-30 08:33:10.331', NULL),
('config_version', 'initial', 'Initial version of the configuration', 5, 'vs3', 0, 'S', '2024-08-30 08:33:10.331', NULL),
('config_version', 'current', 'Current version of the configuration', 6, 'vs4', 0, 'S', '2024-08-30 08:33:10.331', NULL),
('config_version', 'changes', 'Number of time the version was changed', 7, 'vn1', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('config_version', 'size', 'Cumulative Payload size', 8, 'vn2', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('memory_usage', 'server', 'Server Name', 1, 'ks1', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('memory_usage', 'memory_pool', 'Memory Pool Name', 2, 'ks2', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('memory_usage', 'init', 'Initial memory allocation', 3, 'vn1', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('memory_usage', 'used', 'Current memory being utilized', 4, 'vn2', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('memory_usage', 'committed', 'Committed memory available', 5, 'vn3', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('memory_usage', 'max', 'Maximum memory available', 6, 'vn4', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('gc_usage', 'server', 'Server Name', 1, 'ks1', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('gc_usage', 'gc_name', 'Grabage collector name', 2, 'ks2', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('gc_usage', 'gc_count', 'Collection count', 3, 'vn1', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('gc_usage', 'gc_time', 'Collection time', 4, 'vn2', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('cpu_usage', 'server', 'Server Name', 1, 'ks1', 1, 'S', '2024-08-30 08:33:10.331', NULL),
('cpu_usage', 'system_cpu', 'System cpu utilizatiom', 2, 'vn1', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('cpu_usage', 'process_cpu', 'Process cpu utilization', 3, 'vn2', 0, 'N', '2024-08-30 08:33:10.331', NULL),
('cpu_usage', 'system_load', 'Current system load', 4, 'vn3', 0, 'N', '2024-08-30 08:33:10.331', NULL);
