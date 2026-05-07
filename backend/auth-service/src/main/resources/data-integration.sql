INSERT INTO roles (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');
INSERT INTO roles (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO auth_users (id, email, password, email_verified, role_id, created_at)
SELECT 1, 'test@test.com','$2a$10$kvZBLF1rNf.LvwAIYgpc3.DI4q3wNM9Raago3pTeLcalru9kWZTVu', true, r.id, NOW()
FROM roles r
WHERE r.name = 'ROLE_USER'
AND NOT EXISTS (
SELECT 1 FROM auth_users WHERE email = 'test@test.com'
);

INSERT INTO auth_users (id, email, password, email_verified, role_id, created_at)
SELECT 2, 'other@test.com', '$2a$10$kvZBLF1rNf.LvwAIYgpc3.DI4q3wNM9Raago3pTeLcalru9kWZTVu', true, r.id, NOW()
FROM roles r
WHERE r.name = 'ROLE_USER'
AND NOT EXISTS (
SELECT 1 FROM auth_users WHERE email = 'other@test.com'
);
