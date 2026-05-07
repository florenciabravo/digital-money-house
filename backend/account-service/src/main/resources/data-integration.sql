INSERT INTO accounts (id, user_id, balance, cvu, alias)
SELECT 1, 1, 1000.00, '1234567890123456789012', 'test.alias'
WHERE NOT EXISTS (
    SELECT 1 FROM accounts WHERE id = 1
);

INSERT INTO accounts (id, user_id, balance, cvu, alias)
SELECT 2, 2, 500.00, '2222222222222222222222', 'other.alias'
WHERE NOT EXISTS (
    SELECT 1 FROM accounts WHERE id = 2
);

-- Transactions for account_id = 1 (primary test user)
INSERT INTO transactions (account_id, type, amount, related_account_id, description, created_at)
SELECT 1, 'DEPOSIT',  150000.00, NULL, 'Carga de saldo inicial',        '2025-04-10 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE account_id = 1);

INSERT INTO transactions (account_id, type, amount, related_account_id, description, created_at)
VALUES
(1, 'DEPOSIT',   75000.00, NULL, 'Transferencia recibida',        '2025-04-12 11:30:00'),
(1, 'WITHDRAW',  20000.00, NULL, 'Pago de servicio - Luz',        '2025-04-14 14:15:00'),
(1, 'TRANSFER',  50000.00, 12,   'Transferencia a cuenta amigo',  '2025-04-16 16:45:00'),
(1, 'DEPOSIT',   30000.00, 9,    'Transferencia recibida de CVU', '2025-04-18 08:20:00'),
(1, 'WITHDRAW',  10000.00, NULL, 'Pago de servicio - Internet',   '2025-04-19 10:00:00'),
(1, 'TRANSFER',  25000.00, 33,   'Transferencia enviada',         '2025-04-20 18:30:00');