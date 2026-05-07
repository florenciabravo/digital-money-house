INSERT INTO users (id, first_name, last_name)
SELECT 1,'Test','User'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE id = 1
);

INSERT INTO users (id, first_name, last_name)
SELECT 2, 'Other', 'User'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE id = 2
);