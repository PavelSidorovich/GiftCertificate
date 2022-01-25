INSERT INTO tag (name)
VALUES ('Gift');
INSERT INTO tag (name)
VALUES ('Car');
INSERT INTO tag (name)
VALUES ('Family');
INSERT INTO tag (name)
VALUES ('Sports');
INSERT INTO tag (name)
VALUES ('Health');
INSERT INTO tag (name)
VALUES ('New Year');
INSERT INTO tag (name)
VALUES ('Balloon');
INSERT INTO tag (name)
VALUES ('Animal');
INSERT INTO tag (name)
VALUES ('Food');
INSERT INTO tag (name)
VALUES ('Clothes');

INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('Sports Subscription', 'Subscription to the gym', '12.0', '30', '2022-01-23T16:00:00.000000',
        '2022-01-23T16:00:00.000000');
INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('Game club certificate', '', '45.99', '10', '2022-01-23T15:00:00.000000', '2022-01-23T16:00:00.000000');
INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('Swimming pool', 'Certificate for the swimming pool', '25.0', '30', '2022-01-23T16:00:00.000000',
        '2022-01-23T16:00:00.000000');

INSERT INTO gift_certificates_by_tags (gift_certificate_id, tag_id)
VALUES ('1', '1');
INSERT INTO gift_certificates_by_tags (gift_certificate_id, tag_id)
VALUES ('1', '3');
INSERT INTO gift_certificates_by_tags (gift_certificate_id, tag_id)
VALUES ('1', '4');
INSERT INTO gift_certificates_by_tags (gift_certificate_id, tag_id)
VALUES ('2', '1');
INSERT INTO gift_certificates_by_tags (gift_certificate_id, tag_id)
VALUES ('2', '2');
INSERT INTO gift_certificates_by_tags (gift_certificate_id, tag_id)
VALUES ('3', '8');
INSERT INTO gift_certificates_by_tags (gift_certificate_id, tag_id)
VALUES ('3', '9');
INSERT INTO gift_certificates_by_tags (gift_certificate_id, tag_id)
VALUES ('3', '10');