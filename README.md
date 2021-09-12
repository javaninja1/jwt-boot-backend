# JWT Auth Demo using Spring Boot and MYSQL


## Running Spring Boot application
```
create database testdb;
grant all on testdb.* to 'user'@'localhost' identified by 'password';
mvn spring-boot:run
```

## Run following SQL insert statements
```
INSERT INTO roles(name) VALUES('ADMIN');
INSERT INTO roles(name) VALUES('USER');
INSERT INTO roles(name) VALUES('MODERATOR');
```

## Signup REST POST Test
```
###
POST http://localhost:8080/api/auth/signup
Content-Type: application/json

{
"username": "{USERNAME}",
"password": "{PASSWORD}",
"email": "abc@example.com",
"role": ["ROLE_USER"]
}
```

## Login REST POST Test
```
###
POST http://localhost:8080/api/auth/signin
Content-Type: application/json

{
"username": "{USERNAME}",
"password": "{PASSWORD}"
}
```

## Accessing resources using JWT
```
###
GET http://localhost:8080/api/app/user
Content-Type: application/json
Authorization: Bearer {TOKEN}

```

## Using curl as Client
```
TOKEN=$(curl -s -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' \
--data '{"username":"{USER}","password":"{PASS}"}' http://localhost:8080/api/auth/signin | grep-for-token )

curl -H 'Accept: application/json' -H "Authorization: Bearer {TOKEN}" http://localhost:8080/api/app/user
```
## Further Securing tokens using public and private key
```
https://blog.miguelgrinberg.com/post/json-web-tokens-with-public-key-signatures

```