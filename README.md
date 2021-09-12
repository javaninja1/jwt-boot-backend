# JWT Auth Demo using Spring Boot and MYSQL


# For MySQL

```
spring.datasource.url= jdbc:mysql://localhost:3306/testdb?useSSL=false
spring.datasource.username= testuser
spring.datasource.password= testpass
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto= update
```


## Run Spring Boot application
```
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
"username": "user1",
"password": "pass123",
"email": "abc@zzzz.com",
"role": ["ROLE_USER"]
}
```

## Login REST POST Test
```
###
POST http://localhost:8080/api/auth/signin
Content-Type: application/json

{
"username": "{USER}",
"password": "{PASS}"
}
```

## Accessing resources using JWT
```
###
GET http://localhost:8080/api/test/user
Content-Type: application/json
Authorization: Bearer {TOKEN}

```

## Using curl as Client
```
TOKEN=$(curl -s -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' \
--data '{"username":"{USER}","password":"{PASS}"}' http://localhost:8080/api/auth/signin | grep-for-token )

curl -H 'Accept: application/json' -H "Authorization: Bearer {TOKEN}" http://localhost:8080/api/test/user
```
## Further Securing tokens using public and private key
```
https://blog.miguelgrinberg.com/post/json-web-tokens-with-public-key-signatures

```