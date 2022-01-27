DROP FUNCTION find_certificates_by_filter;
CREATE
OR
REPLACE FUNCTION find_certificates_by_filter(_tag_name varchar(128),
    _certificate_name varchar (128),
    _description varchar (256)
    )
    RETURNS TABLE
(_gift_certificate_id bigint)
    LANGUAGE plpgsql
    AS
    $func$
BEGIN RETURN QUERY
SELECT DISTINCT gift_certificate.id
FROM gift_certificates_by_tags
         RIGHT JOIN tag ON tag_id = tag.id
         RIGHT JOIN gift_certificate ON gift_certificate_id = gift_certificate.id
WHERE (tag.name = _tag_name OR _tag_name IS NULL)
  AND (gift_certificate.name LIKE _certificate_name OR _certificate_name IS NULL)
  AND (gift_certificate.description LIKE _description OR _description IS NULL)
GROUP BY gift_certificate.id;
END
$func$;