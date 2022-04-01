INSERT INTO role (role_name)
VALUES ('ROLE_ADMIN');
INSERT INTO role (role_name)
VALUES ('ROLE_USER');
INSERT INTO role (role_name)
VALUES ('ROLE_GUEST');

INSERT INTO account (email, account_password, is_enabled)
VALUES ('example1@gmail.com', '$2a$10$c2sygaSGY30u/NnW1zGkBu0qD68rpDMpJjsb5JqwhGtQEnRwGSJWy', true);

INSERT INTO accounts_by_roles
VALUES (1, 1)
