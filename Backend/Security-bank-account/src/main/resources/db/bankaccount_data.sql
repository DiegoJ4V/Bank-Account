INSERT INTO `account` (account_name, email, password, current_balance, enabled) VALUES
('Random1', 'random1@names.com', '123456', 10000.00, 1),
('Random2', 'random2@names.com', '123456', 32000.00, 1),
('Random3', 'random3@names.com', '123456', 4000.00, 1),
('Random4', 'random4@names.com', '123456', 154354.00, 0),
('Random5', 'random5@names.com', '123456', 8677.00, 0);

INSERT INTO `verification_token` (token, expiration_time, id_account) VALUES
('er143ge8-9b58-41ae-8723-29d7ff675a30', {ts '2021-12-25 13:47:05.602'}, 4),
('7f1a71e8-9b58-41ae-8723-29d7ff675a30', {ts '2022-12-25 14:10:05.602'}, 5);