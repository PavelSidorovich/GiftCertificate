-- populate roles
INSERT INTO role (role_name)
VALUES ('ROLE_ADMIN');
INSERT INTO role (role_name)
VALUES ('ROLE_USER');
INSERT INTO role (role_name)
VALUES ('ROLE_GUEST');

-- populate users
INSERT INTO account (email, account_password, is_enabled)
VALUES ('example1@gmail.com', '$2a$10$c2sygaSGY30u/NnW1zGkBu0qD68rpDMpJjsb5JqwhGtQEnRwGSJWy', true);
INSERT INTO account (email, account_password, is_enabled)
VALUES ('example2@gmail.com', '$2a$10$c2sygaSGY30u/NnW1zGkBu0qD68rpDMpJjsb5JqwhGtQEnRwGSJWy', true);
INSERT INTO account (email, account_password, is_enabled)
VALUES ('example3@gmail.com', '$2a$10$c2sygaSGY30u/NnW1zGkBu0qD68rpDMpJjsb5JqwhGtQEnRwGSJWy', true);
INSERT INTO account (email, account_password, is_enabled)
VALUES ('example4@gmail.com', '$2a$10$c2sygaSGY30u/NnW1zGkBu0qD68rpDMpJjsb5JqwhGtQEnRwGSJWy', true);

INSERT INTO user_detail (id, first_name, last_name, balance)
VALUES (2, 'Pavel', 'Ivanov', 1500);
INSERT INTO user_detail (id, first_name, last_name, balance)
VALUES (3, 'Artur', 'Kim', 400);
INSERT INTO user_detail (id, first_name, last_name, balance)
VALUES (4, 'Nikita', 'Semchenkov', 100);

INSERT INTO accounts_by_roles (account_id, role_id)
VALUES (1, 1);
INSERT INTO accounts_by_roles (account_id, role_id)
VALUES (2, 2);
INSERT INTO accounts_by_roles (account_id, role_id)
VALUES (3, 2);
INSERT INTO accounts_by_roles (account_id, role_id)
VALUES (4, 2);

-- populate tags
INSERT INTO certificate_tag (name)
VALUES ('gift');
INSERT INTO certificate_tag (name)
VALUES ('family');
INSERT INTO certificate_tag (name)
VALUES ('sport');
INSERT INTO certificate_tag (name)
VALUES ('toy');
INSERT INTO certificate_tag (name)
VALUES ('health');
INSERT INTO certificate_tag (name)
VALUES ('tech');
INSERT INTO certificate_tag (name)
VALUES ('learning');

-- populate certificates
INSERT INTO certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('Sport wear certificate', 'discount for sportswear', 50, 30, now(), now());
INSERT INTO certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('Free testing of beds', '', 0, 5, now(), now());
INSERT INTO certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('English courses', 'English speaking B2+', 100, 60, now(), now());
INSERT INTO certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('Kids robo-lab', '', 150, 30, now(), now());
INSERT INTO certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('Family trip to Big Ben', 'visit this wonderful place', 140, 4, now(), now());

INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (1, 3);
INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (1, 5);
INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (2, 5);
INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (3, 1);
INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (3, 2);
INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (3, 7);
INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (4, 3);
INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (4, 6);
INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (4, 7);
INSERT INTO certificates_by_tags (certificate_id, tag_id)
VALUES (5, 2);

-- populate user orders
INSERT INTO user_order (user_id, certificate_id, total_cost, purchase_date)
VALUES (2, 1, 50, now());
INSERT INTO user_order (user_id, certificate_id, total_cost, purchase_date)
VALUES (2, 3, 110, now());
INSERT INTO user_order (user_id, certificate_id, total_cost, purchase_date)
VALUES (2, 5, 130, now());
INSERT INTO user_order (user_id, certificate_id, total_cost, purchase_date)
VALUES (3, 1, 50, now());
INSERT INTO user_order (user_id, certificate_id, total_cost, purchase_date)
VALUES (3, 2, 110, now());
INSERT INTO user_order (user_id, certificate_id, total_cost, purchase_date)
VALUES (3, 3, 100, now());
INSERT INTO user_order (user_id, certificate_id, total_cost, purchase_date)
VALUES (3, 3, 100, now());
INSERT INTO user_order (user_id, certificate_id, total_cost, purchase_date)
VALUES (4, 4, 140, now());
INSERT INTO user_order (user_id, certificate_id, total_cost, purchase_date)
VALUES (4, 5, 150, now());
