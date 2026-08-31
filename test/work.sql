SELECT b.professional_id AS id, a.full_name, d.name, d.duration_minutes
  FROM fks_users a
 INNER JOIN fks_professionals b ON (a.user_id = b.user_id AND a.role = 'PROFESSIONAL')
 INNER JOIN fks_professional_services c ON (b.professional_id = c.professional_id)
 INNER JOIN fks_services d ON (c.service_id = d.service_id)
 WHERE b.professional_id = 2;
 
 
 SELECT b.professional_id AS id, a.full_name, d.name, d.duration_minutes, e.date, e.start_time, e.end_time
  FROM fks_users a
 INNER JOIN fks_professionals b ON (a.user_id = b.user_id AND a.role = 'PROFESSIONAL')
 INNER JOIN fks_professional_services c ON (b.professional_id = c.professional_id)
 INNER JOIN fks_services d ON (c.service_id = d.service_id AND d.service_id = 272)
 INNER JOIN fks_availability e ON (b.professional_id = e.professional_id AND e.date = '2026-08-23')
 
  SELECT b.professional_id AS id, a.full_name, d.name, d.duration_minutes
  FROM fks_users a
 INNER JOIN fks_professionals b ON (a.user_id = b.user_id AND a.role = 'PROFESSIONAL')
 INNER JOIN fks_professional_services c ON (b.professional_id = c.professional_id)
 INNER JOIN fks_services d ON (c.service_id = d.service_id AND d.service_id = 272)
 

-- Check availability of professionals for a specific service.

SELECT service_id, name, duration_minutes, professional_id, date, COUNT(*)
  FROM (
        SELECT a.service_id, a.name, a.duration_minutes, b.professional_id, c.date, c.start_time, c.end_time, c.is_booked
          FROM fks_services a
         INNER JOIN fks_professional_services b ON (a.service_id = b.service_id)
         INNER JOIN fks_availabilities c ON (b.professional_id = c.professional_id AND c.date = '2026-08-25' AND c.start_time >= TIME '09:00:00' AND end_time <= TIME '10:00:00' AND is_booked = 0)
         WHERE a.service_id = 271
       )
 GROUP BY service_id, name, duration_minutes, professional_id, date
HAVING COUNT(*) = 5;



WITH avail_status AS (
    SELECT d.professional_id
           , d.date
           , d.start_time AS slot_1
           , CASE LEAD(d.start_time, 0) OVER w
                WHEN '17:00:00'::time THEN '18:00:00'::time
                ELSE LEAD(d.start_time, 1) OVER w
             END AS slot_2
      FROM fks_services a
     INNER JOIN fks_professional_services b ON (a.service_id = b.service_id)
     INNER JOIN fks_professionals c ON (b.professional_id = c.professional_id AND c.is_verified = 1)
     INNER JOIN fks_availabilities d ON (c.professional_id = d.professional_id AND d.date = '2026-08-29' AND d.is_booked = 0)
     WHERE a.service_id = 1
    WINDOW w AS (
        PARTITION BY d.professional_id, d.date
        ORDER BY d.start_time
    )
)
, prof_avail_status AS (
    SELECT professional_id, date, slot_1, slot_2
      FROM avail_status
     WHERE EXTRACT(HOUR FROM (slot_2 - slot_1)) = 1
)
SELECT slot_1 AS from_time, slot_2 AS to_time, COUNT(*) AS slots
  FROM prof_avail_status
 GROUP BY slot_1, slot_2
 ORDER BY slot_1, slot_2


WITH avail_status AS (
    SELECT d.professional_id
           , d.date
           , d.start_time AS slot_1
           , CASE LEAD(d.start_time, 0) OVER w
                WHEN '17:00:00'::time THEN '18:00:00'::time
                ELSE LEAD(d.start_time, 1) OVER w
             END AS slot_2
           , CASE LEAD(d.start_time, 1) OVER w
                WHEN '17:00:00'::time THEN '18:00:00'::time
                ELSE LEAD(d.start_time, 2) OVER w
             END AS slot_3
           , CASE LEAD(d.start_time, 2) OVER w
                WHEN '17:00:00'::time THEN '18:00:00'::time
                ELSE LEAD(d.start_time, 3) OVER w
             END AS slot_4
           , CASE LEAD(d.start_time, 3) OVER w
                WHEN '17:00:00'::time THEN '18:00:00'::time
                ELSE LEAD(d.start_time, 4) OVER w
             END AS slot_5
           , CASE LEAD(d.start_time, 4) OVER w
                WHEN '17:00:00'::time THEN '18:00:00'::time
                ELSE LEAD(d.start_time, 5) OVER w
             END AS slot_6
      FROM fks_services a
     INNER JOIN fks_professional_services b ON (a.service_id = b.service_id)
     INNER JOIN fks_professionals c ON (b.professional_id = c.professional_id AND c.is_verified = 1)
     INNER JOIN fks_availabilities d ON (c.professional_id = d.professional_id AND d.date = '2026-08-25' AND d.is_booked = 0)
     WHERE a.service_id = 336
    WINDOW w AS (
        PARTITION BY d.professional_id, d.date
        ORDER BY d.start_time
    )
)
, prof_avail_status AS (
    SELECT professional_id, date, slot_1, slot_2, slot_3, slot_4, slot_5, slot_6
      FROM avail_status
     WHERE EXTRACT(HOUR FROM (slot_2 - slot_1)) = 1
       AND EXTRACT(HOUR FROM (slot_3 - slot_2)) = 1
       AND EXTRACT(HOUR FROM (slot_4 - slot_3)) = 1
       AND EXTRACT(HOUR FROM (slot_5 - slot_4)) = 1
       AND EXTRACT(HOUR FROM (slot_6 - slot_5)) = 1
)
SELECT slot_1, slot_6, COUNT(*) AS slots
  FROM prof_avail_status
 GROUP BY slot_1, slot_6


WITH params AS (
    SELECT
        DATE '2026-08-26' AS booking_date,
        TIME '12:00:00' AS work_start,
        TIME '17:00:00' AS work_end
),
prof_eligibility AS (
    SELECT b.professional_id, b.date, p.work_start, p.work_end, MIN(b.start_time) AS start_time, MAX(b.end_time) AS end_time, COUNT(*) AS slot_count
      FROM fks_professional_services a
     INNER JOIN fks_availabilities b ON (a.professional_id = b.professional_id AND a.service_id = 336)
     CROSS JOIN params p
    WHERE date = p.booking_date
      AND b.start_time >= p.work_start
      AND b.end_time <= p.work_end
      AND b.is_booked = 0
      AND NOT EXISTS (
        SELECT 1
          FROM fks_availabilities c
         WHERE b.professional_id = c.professional_id
           AND b.date = c.date
           AND c.is_booked = 1
      )
    GROUP BY b.professional_id, b.date, p.work_start, p.work_end
    HAVING COUNT(*) = EXTRACT (HOUR FROM (p.work_end - p.work_start))
       AND MIN(b.start_time) = p.work_start
       AND MAX(b.end_time) = p.work_end
)
SELECT a.*
  FROM fks_availabilities a
 INNER JOIN prof_eligibility e ON (e.professional_id = a.professional_id AND e.date = a.date)
 CROSS JOIN params p
 WHERE a.start_time >= p.work_start
   AND a.end_time <= p.work_end
 ORDER BY a.professional_id, a.start_time
 LIMIT (SELECT EXTRACT(HOUR FROM (work_end - work_start))
          FROM params)
 FOR UPDATE;


SELECT a.booking_id
        , a.scheduled_at
        , a.time_slot
        , a.status
        , a.payment_method
        , a.total_amount
        , a.created_at
        , b.service_id
        , b.name
        , c.address_line1
        , c.address_line2
        , c.city
        , c.pincode
        , COALESCE(e.full_name, 'Professional not assigned') AS professional_name
        , COALESCE(e.phone1, '') AS phone1
  FROM fks_bookings a
 INNER JOIN fks_services b ON (a.service_id = b.service_id)
 INNER JOIN fks_addresses c ON (a.address_id = c.address_id)
 LEFT OUTER JOIN fks_professionals d ON (a.professional_id = d.professional_id)
 LEFT OUTER JOIN fks_users e ON (d.user_id = e.user_id AND e.role = 'PROFESSIONAL')
 WHERE a.customer_id = 119 
 ORDER BY a.created_at DESC;



-- Output
-- 09:00 - 10:00 : 3
-- 10:00 - 11:00 : 7
-- 11:00 - 12:00 : 1
-- 11:00 - 12:00 : 1
-- ....
-- ....

SELECT a.service_id, a.name, a.duration_minutes, b.professional_id, c.date, c.start_time, c.end_time, c.is_booked
  FROM fks_services a
 INNER JOIN fks_professional_services b ON (a.service_id = b.service_id)
 INNER JOIN fks_availabilities c ON (b.professional_id = c.professional_id AND c.date = '2026-08-25' AND c.start_time >= TIME '09:00:00' AND c.end_time <= c.start_time + CEIL(a.duration_minutes / 60.0) * INTERVAL '1 hour' AND c.is_booked = 0)
 WHERE a.service_id = 271

-- Newly Added Categories + Sub-Categories + Services --

folksdb=> SELECT COUNT(*)
            FROM fks_categories
           WHERE category_id IN (101, 102, 103, 104, 105);
 count 
-------
     5

folksdb=> SELECT COUNT(*)
            FROM fks_categories
           WHERE parent_id IN (101, 102, 103, 104, 105);
 count 
-------
    14

folksdb=> SELECT COUNT(*)
            FROM fks_services a
           INNER JOIN fks_categories b ON (a.category_id = b.category_id AND b.parent_id IN (101, 102, 103, 104, 105));
 count 
-------
    69

folksdb=> SELECT MIN(service_id) AS service_id_min, MAX(service_id) AS service_id_max
            FROM fks_services a
           INNER JOIN fks_categories b ON (a.category_id = b.category_id AND b.parent_id IN (101, 102, 103, 104, 105));

service_id_min | service_id_max 
----------------+----------------
            271 |            339


--------------------------------------------------------------------------


-- Newly Added Professionals (552) --

SELECT MIN(b.professional_id) AS professional_start, MAX(b.professional_id) AS professional_end, MAX(b.professional_id) - MIN(b.professional_id) + 1 AS total
  FROM fks_users a
 INNER JOIN fks_professionals b ON (a.user_id = b.user_id AND a.user_id BETWEEN 149 AND 700);

 professional_start | professional_end | total 
--------------------+------------------+-------
                 31 |              582 |   552


SELECT service_id, COUNT(*)
  FROM fks_professional_services 
 WHERE professional_id BETWEEN 31 AND 582
   AND service_id BETWEEN 271 AND 339
 GROUP BY service_id
 HAVING COUNT(*) = 8


SELECT COUNT(*)
  FROM (
        SELECT service_id, COUNT(*)
          FROM fks_professional_services 
         WHERE professional_id BETWEEN 31 AND 582
           AND service_id BETWEEN 271 AND 339
         GROUP BY service_id
        HAVING COUNT(*) = 8
);

Total new professionals added = 552
fks_users                   => 149 - 700
fks_professionals           => 31 - 582
fks_professional_services   => 69




INSERT INTO fks_availabilities (professional_id, date, start_time, end_time, is_booked, created_at)
SELECT
    p.professional_id
    , d::date AS date
    , CAST ('09:00:00' + (h * INTERVAL '1 hour') AS TIME) AS start_time
    , CAST ('09:00:00' + ((h + 1) * INTERVAL '1 hour') AS TIME) AS end_time
    , 0 AS is_booked
    , CAST ((TO_CHAR(CURRENT_DATE, 'yyyy-mm-dd') || ' 00:00:00') AS TIMESTAMP) AS created_at
  FROM fks_professionals p
 CROSS JOIN generate_series(
       CAST (TO_CHAR(CURRENT_DATE, 'yyyy-mm-dd') AS DATE),
       CAST (TO_CHAR(CURRENT_DATE + 4, 'yyyy-mm-dd') AS DATE),
       INTERVAL '1 day'
 ) AS d
 CROSS JOIN generate_series(0, 8) AS h
 WHERE p.professional_id BETWEEN 1 AND 2
 ORDER BY p.professional_id, date, start_time;

