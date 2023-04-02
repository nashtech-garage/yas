START TRANSACTION;

INSERT INTO users(email)
SELECT
  'user_' || seq || '@' || (
    CASE (RANDOM() * 2)::INT
      WHEN 0 THEN 'gmail'
      WHEN 1 THEN 'hotmail'
      WHEN 2 THEN 'yahoo'
    END
  ) || '.com' AS email
FROM GENERATE_SERIES(1, 5) seq;


INSERT INTO posts(user_id, title)
WITH expanded AS (
  SELECT RANDOM(), seq, u.id AS user_id
  FROM GENERATE_SERIES(1, 25) seq, users u
), shuffled AS (
  SELECT e.*
  FROM expanded e
  INNER JOIN (
    SELECT ei.seq, MIN(ei.random) FROM expanded ei GROUP BY ei.seq
  ) em ON (e.seq = em.seq AND e.random = em.min)
  ORDER BY e.seq
)
SELECT
  s.user_id,
  'It is ' || s.seq || ' ' || (
    CASE (RANDOM() * 2)::INT
      WHEN 0 THEN 'sql'
      WHEN 1 THEN 'elixir'
      WHEN 2 THEN 'ruby'
    END
  ) as title
FROM shuffled s;


INSERT INTO comments(user_id, post_id, body)
WITH expanded AS (
  SELECT RANDOM(), seq, u.id AS user_id, p.id AS post_id
  FROM GENERATE_SERIES(1, 100) seq, users u, posts p
), shuffled AS (
  SELECT e.*
  FROM expanded e
  INNER JOIN (
    SELECT ei.seq, MIN(ei.random) FROM expanded ei GROUP BY ei.seq
  ) em ON (e.seq = em.seq AND e.random = em.min)
  ORDER BY e.seq
)
SELECT
  s.user_id,
  s.post_id,
  'Here some comment ' || s.seq AS body
FROM shuffled s;

COMMIT;
