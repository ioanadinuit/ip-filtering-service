# Ip Filtering By Rules Service

This service aims to implement ip filtering rules for an IP tuple: source - destination.

It has the following endpoints:


### Create ip rules for filtering by range
POST http://localhost:8080/ip-filter-rules

#### {
#### "sourceStartIp": "192.10.10.10",
#### "sourceEndIp": "192.10.10.11",
#### "destinationStartIp": "193.10.10.10",
#### "destinationEndIp": "193.10.10.12",
#### "allow": true
#### }

### Create ip rules for filtering by Subnet Mask

POST http://localhost:8080/ip-filter-rules
#### {
#### "sourceSubnetIp": "192.10.10.10/32",
#### "destinationSubnetIp": "192.10.10.10/32",
#### "allow": true
#### }

### Get if an Ip is allowed or not

It returns FALSE if no matching range/mask was found

GET http://localhost:8080/ip-filter-rules/allowed?sourceIp=193.0.0.1&destinationIp=192.0.0.1

### Get by ID
GET http://localhost:8080/ip-filter-rules/12

### DELETE by ID
DELETE http://localhost:8080/ip-filter-rules/8

### Database: postgresql 13

Setup a local db connection to run the application

1. url: "jdbc:postgresql://127.0.0.1:5432/ip_rules_db"
2. username: postgres
3. password: root

### Run the application:

1. git clone https://github.com/ioanadinuit/ip-filtering-service.git
2. cd ip-filtering-service
3. mvn clean install
4. java -jar target/ip_filter-service-1.0-RELEASE.jar

### Technical information
1. Postgres was used becauseof the inet and cidr support it offers
2. The application stores all the rules via CREATE but for filtering it uses a complex SQL Query
3. Because of the ::inet refferences JPA has issues in mapping inpit variables such as :sourceIp and :destinationIp
4. Instead an parsable by String.format(...) query is loaded and executed via a prepared statement
5. Resolving at database level the filtering, makes the application capable of storing millions of rules whitound the need to access them to be able to filter
6. Thus, is capable of high traffic with small pressure on memory and filtering performance
