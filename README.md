#Soccer Manager Game

## HOWTO

### Build
``mvn package``

Requirements:
* _JDK 8 or higher_

### Run

1) Start application server:

``java -jar soccermanager.jar``

OR

``mvn spring-boot:run``

2) Open [http://localhost:8080](http://localhost:8080) in the browser.

## Documentation

### Architecture

1. `doc/Architecture.png` - Component architecture.
2. `doc/Entities.png` - Domain entities.

### Open API Specification

1. `doc/AuthService.oas.yaml` - AuthService REST API.
2. `doc/ManagerService.oas.yaml` - ManagerService REST API.

OAS files can be visualized in [Swagger editor](http://editor.swagger.io/).
