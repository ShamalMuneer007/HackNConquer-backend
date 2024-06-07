# HackNConquer
## Java SpringBoot Backend

HackNConquer is a dynamic competitive programming platform where users can solve coding problems, connect and compete with fellow coders worldwide.
This repository hosts the microservices that power the backend of HackNConquer. The services are containerized using Docker and orchestrated using Kubernetes.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

This repository contains a microservices-based application built using Spring Boot and Spring Cloud. Below is a brief description of each service.

## Services

- **Api Gateway Service:** Acts as the entry point for all client requests, routing them to the appropriate microservices.
- **User Service:** Manages user information and authentication.
- **Clan Service:** Handles clan creation, management, and related functionalities.
- **Chat Service:** Provides real-time messaging capabilities between users.
- **Discussion Service:** Facilitates forums and discussion boards for users.
- **Payment Service:** Manages payment processing, premium-subscriptions and transactions.
- **Config Server:** Centralized configuration management for all microservices.
- **Name Server:** Eureka discovery service for service registration and discovery.
- **Problem Service:** Manages problem statements and related content for users to solve.
- **Test Service:** Handles the execution and management of test cases for user-submitted solutions.
- **Submission Service:** Manages the submission of solutions and tracks their status.
- **Notification Service:** Sends notifications to users based on various events and triggers.

## Getting Started

### Prerequisites

- JDK-17 
- MySQL
- MongoDB
- Redis
- RabbitMQ

### Installation

To run the application, follow these steps:

1. Clone the repository: `git clone https://github.com/ShamalMuneer007/HackNConquer-backend`
2. Navigate to the project directory: `cd <project-directory>`
3. Start the Config Server: `mvn spring-boot:run -pl config-server`
4. Start the Name Server (Eureka): `mvn spring-boot:run -pl name-server`
5. Start the remaining services in any order: `mvn spring-boot:run -pl <service-name>`

## Configuration

Ensure all services are correctly configured to point to the Config Server and Name Server. Configuration files are located in the `config` directory.

## Contributing

Feel free to submit issues and pull requests. For major changes, please open an issue first to discuss what you would like to change.
