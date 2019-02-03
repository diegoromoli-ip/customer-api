# customer-api

## Design

A few assumptions have been made while implementing this API. The time frame for this implementation made made it necessary to 
cut some corners and so this implementation should be considered a POC. 

The security mechanism is Basic Authentication. It can be changed to OAuth, for example, if more time were allowed.

The API accepts and returns either XML or JSON. 

## Installation

Clone the project with:

```
$ git clone https://github.com/diegoromoli-ip/customer-api.git
```

Run it with:

``` 
$ cd customer-api
$ mvn clean package
$ java -jar target/customer-api-0.0.1-SNAPSHOT.jar
```

## Swagger UI

To access the Swagger UI, open a browser pointing to:

```
http://localhost:8080/swagger-ui.html
```

## Logging-in and existing users

There are four existing users. Only one of them has an existing customer profile. This profile can be retrieved and updated.

The other users will need a profile created first.

These are the existing users and their passwords:

```
customer1/password1   <--  This user has an existing customer profile
customer2/password2
customer3/password3
customer4/password4
```

