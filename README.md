# restapi-reactive
Basic REST API built with a reactive stack

This is a very basic REST API with a single resource. The project has 96% test coverage (I don't test config 
code and usually aim for 80%).

Store
```json
{
    "id": "5e989e63aea74d64e63709f9",
    "name": "Store 8",
    "address": "132 High St",
    "phone": "3039987473",
    "status": "OPEN"
}
```

Dependencies used:
* Spring Boot 2.2.6
    * JUnit 5
    * Mockito
* Spring Webflux
* Spring Data Reactive MongoDB
* Reactor Test
* MapStruct
* Lombok

## Running the project

The project needs an unsecured MongoDB instance running on localhost with the default port. If you have docker just use 
the following command:
```
docker run -d -p 27017:27017 --name mongodb -v ~/data:/data/db mongo:4.2.3
```

Then you can either launch the application in your IDE, or use Maven to create a jar and execute the jar.

The project includes a Postman collection you can use to exercise the API in the postman folder.

## Package Structure

Historically, your typical package structure has looked like this:

```
|- exception
|- repository
|- service
|- controller
|- util
```

A structure like the one above means that changes usually end up spanning multiple packages. Instead, I prefer to have 
a package for each feature group or bounded context. The repository, service, controller, and all related files live in 
the same package which helps with readability and a step toward isolated (loosely coupled, highly cohesive) components.
Most methods can also be package protected instead of public forcing us to be explicit about what we need to expose to 
other parts of the application. This structure also makes it much easier to break out a microservice if/when this is 
deemed necessary.

```
|- exception
|- store
|- order
|- shipping
```

Notice we don't have a util package(code smell)!

## Exception Package

The exception package and it's tests have no dependencies on the other packages and could easily be pulled into some 
common jar or even a dedicated exception handling lib.

It handles most bad input/bad request related exceptions and shapes them into meaningful responses, including sending 
 back valid values for any invalid Enum.