# Rohlik Store

**Test application of an online store for Rohlik Group**.

The main purpose of this application is to demonstrate the functionality of the online store within the technical specifications set by the company.

## Features

- **Product Management**: Create, update, delete, and view products.
- **Order Management**: Create and manage customer orders.
- **Scheduling**: Automatically delete expired orders.
- **Swagger Integration**: Test the application manually through Swagger.
- **JUnit and Integration Tests**: Verify the operations available in the application.

## Architecture and Technical Solutions

The application is built using the following technologies:
- **Java**: The primary programming language.
- **Spring Boot**: Framework for building the application.
- **Maven**: Build and dependency management tool.
- **H2 Database**: In-memory database for testing.
- **Swagger**: API documentation and testing tool.
- **JUnit**: Unit testing framework.
- **Docker**: Containerization tool.

### Application Structure

- **Controller Layer**: Handles HTTP requests and responses.
- **Service Layer**: Contains business logic.
- **Repository Layer**: Interacts with the database.
- **Model Layer**: Defines the data structures.

### Technical Solutions

- **Scheduling**: Implemented using `@EnableScheduling` to delete expired orders.
- **Exception Handling**: Global exception handler to manage application errors.
- **Docker**: The application is ready to run in a Docker container.

## Testing

The application is available for testing through Swagger. Additionally, JUnit and integration tests are implemented to verify the operations available in the application.

## Future Improvements

To enhance the application's efficiency and performance, the following improvements can be made:
- **Asynchronous Task Queue**: Replace scheduling with an asynchronous task queue using Kafka or RabbitMQ for better performance in high-load scenarios.
- **Cloud Deployment**: Deploy the application to a cloud service and manage it with Kubernetes for better scalability and reliability.

## Feature Enhancements

To expand the application's functionality, the following enhancements can be made:
- **Frontend Development**: Currently, the application does not have a frontend, but it can be developed later.
- **Localization System**: Implement a national localization system for the product catalog, including different language versions of product names, measurement units, and currencies for respective countries.
- **Product Data Enhancement**: Expand the product details to include product images, packaging information, size and weight, and descriptions of consumer qualities.

## Scalability

The application is currently ready to run in a Docker container. In the future, it can be deployed to a cloud service and managed with Kubernetes. This will allow for better scalability and reliability.

## Known Issues

- **Scheduling**: The current implementation of deleting expired orders using scheduling is not optimal for high-load applications. It can be improved by implementing an asynchronous task queue with Kafka or RabbitMQ.

## Conclusion

The Rohlik Store application is a demonstration of an online store's functionality. It is currently available for testing and can be improved and scaled in the future.
