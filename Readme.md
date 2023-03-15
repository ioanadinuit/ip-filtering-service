#Ip Filtering By Rules Service

This service aims to implement ip filtering rules for an IP tuple: source - destination.

It has the following endpoints:


### Create ip rules for filtering by range
POST http://localhost:8080/ip-filter-rules

####{
####"sourceStartIp": "192.10.10.10",
####"sourceEndIp": "192.10.10.11",
####"destinationStartIp": "193.10.10.10",
####"destinationEndIp": "193.10.10.12",
####"allow": true
####}

### Create ip rules for filtering by Subnet Mask

POST http://localhost:8080/ip-filter-rules
####{
####"sourceSubnetIp": "192.10.10.10/32",
####"destinationSubnetIp": "192.10.10.10/32",
####"allow": true
####}

###Get if an Ip is allowed or not

It returns FALSE if no matching range/mask was found

GET http://localhost:8080/ip-filter-rules/allowed?sourceIp=193.0.0.1&destinationIp=192.0.0.1

###Get by ID
GET http://localhost:8080/ip-filter-rules/12

###DELETE by ID
DELETE http://localhost:8080/ip-filter-rules/8


###Run the application:

1. 
