## REST API Basics (second module)

<hr>

### 1. Actions with tags

- #### Creation (POST method)
    - ```http://url/tags```
    - JSON message example:

```
{
    "name":"shop"
}
```

- #### Getting (GET method)
    - ```http://url/tags``` — get all tags
    - ```http://url/tags/{id}``` — get tag with specified id

- #### Deleting (DELETE method)
    - ```http://url/tags/{id}``` — deletes tag with specified id

<hr>

### 2. Actions with certificates

- #### Creation (POST method)
    - ```http://url/certificates```
    - JSON message example:

```
{
        "name": "Certificate name",
        "description":"Description",
        "price": 50,
        "duration": 30,
        "tags": [
            {
                "name": "Pleasure"
            },
            {
                "name": "Gift"
            }
        ]
}
```

- #### Getting (GET method)
    - ```http://url/certificates``` — get all certificates. Additional url parameters are:
        - tagName — find certificates with tag
        - certificateName — search certificates by part of name
        - description — search certificates by part of description
        - sortByCreatedDate — sort certificates by create date (ASC or DESC value)
        - sortByName — sort certificates by name (ASC or DESC value)
    - ```http://url/certificates/{id}``` — get certificate with specified id

- #### Updating (PATCH method)
    - ```http://url/certificates```
    - JSON message example:

```
{
        "name": "Massage certificate for month",
        "price": 60,
        "tags": [
            {
                "name": "Pleasure"
            },
            {
                "name": "Massage"
            }
        ]
}
```

<i><b>Note:</b></i> all json parameters except name are optional

- #### Deleting (DELETE method)
    - ```http://url/certificates/{id}``` — deletes certificate with specified id