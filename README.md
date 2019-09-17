# Invoicing Service #

Invoicing Service is a simple accounting application with possibility to manage invoices. 
There are multiple implementations of databases. 
You can use REST,  SOAP or simple front-end to communicate with application. Application is licensed by [MIT](https://opensource.org/licenses/mit-license.php)

## Tech/frameworks used ##

<img src="https://whirly.pl/wp-content/uploads/2017/05/spring.png" width="200"><img src="http://yaqzi.pl/wp-content/uploads/2016/12/apache_maven.png" width="200"><img src="https://upload.wikimedia.org/wikipedia/commons/2/2c/Mockito_Logo.png" width="200"><img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTNkximiwITI1smJcOkn_bx2Zk_RnNKnmDq23Ua26wTVd_YNJcWgw" width="200"><img src="https://shiftkeylabs.ca/wp-content/uploads/2017/02/JUnit_logo.png" width="200"><img src="https://jules-grospeiller.fr/media/logo_competences/lang/json.png" width="200"><img src="http://www.postgresqltutorial.com/wp-content/uploads/2012/08/What-is-PostgreSQL.png" width="200"><img src="https://cdn.bulldogjob.com/system/readables/covers/000/001/571/thumb/27-02-2019.png" width="200"><img src="https://i2.wp.com/bykowski.pl/wp-content/uploads/2018/07/hibernate-2.png?w=300" width="200"><img src="https://zdnet3.cbsistatic.com/hub/i/r/2018/02/16/8abdb3e1-47bc-446e-9871-c4e11a46f680/resize/370xauto/8a68280fd20eebfa7789cdaa6fb5eff1/mongo-db-logo.png" width="200"><img src="http://mapstruct.org/images/mapstruct.png" width="200">

## Instalation ##

* JDK 11
* Apache Maven 3.x

## Build and Run ##
```
mvn clean package:
mvn exec:java
```
## API ##

Application is available on localhost:8080. Use ```http://localhost:8080/swagger-ui.html```
to test all possibilities of Invoice API. You have to log in and configure login and password in [application.properties](https://github.com/CodersTrustPL/project-13-zuzanna-radek-mateusz-radek-przemek/blob/master/src/main/resources/application.properties):

```
spring.security.user.name=user
spring.security.user.password=pass
```
To receive an invoice by email, go to [email.properties](https://github.com/CodersTrustPL/project-13-zuzanna-radek-mateusz-radek-przemek/blob/master/src/main/resources/email.properties) and set your email.

```
spring.mail.host=yourSmtp
spring.mail.port=yourPort
spring.mail.username=yourUserName
spring.mail.password=yourPassword
```

To test SOAP, use [Postman](https://www.getpostman.com) or another tool. To create some request use
```localhost:8080/soap/invoices/invoices.wsdl```

## Setup Database ##

To change current database go to [application.properties](https://github.com/CodersTrustPL/project-13-zuzanna-radek-mateusz-radek-przemek/blob/master/src/main/resources/application.properties). You can choose in-file, in-memory, mongo or hibernate database
```
   pl.coderstrust.database=in-file
   pl.coderstrust.database=in-memory
   pl.coderstrust.database=hibernate
   pl.coderstrust.database=mongo
```
Application works correctly without hibernate and mongo database.

To use **hibernate**, first configure it on your computer (using PgAdmin or another tool) and in [hibernate.properties](https://github.com/CodersTrustPL/project-13-zuzanna-radek-mateusz-radek-przemek/blob/master/src/main/resources/hibernate.properties)
```
spring.datasource.url=yourDatabase
spring.datasource.username=yourUserName
spring.datasource.password=yourPassword
```

To use **mongo**, first configure it on your computer and in [mongo.properties](https://github.com/CodersTrustPL/project-13-zuzanna-radek-mateusz-radek-przemek/blob/master/src/main/resources/mongodb.properties)
```
spring.data.mongodb.database=invoicedb
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
```

## For an end User ##

```http://localhost:8080/```
Allows you to view invoice data in the database, search invoice, download pdf, delete the invoice and view invoice details.
