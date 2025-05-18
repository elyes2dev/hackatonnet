# HackathonNet Backend 🚀  
*Spring Boot REST API for Hackathon Management System*

## Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Authentication](#-authentication)
- [Deployment](#-deployment)
- [Team](#-team)
- [License](#-license)

## ✨ Features

### Core Functionality
- **Multi-role System** (Organizers/Participants/Mentors/Admins)
- **JWT Authentication** with role-based access control
- **Real-time WebSocket** for chat and notifications
- **AI Mentor Matching** algorithm
- **File Upload** system for project submissions
- **Workshop Management** with scheduling

### Advanced Features
- 🕒 Event scheduling with conflict detection
- 📊 Analytics endpoints for dashboard data
- 🤖 AI-powered team formation suggestions
- 🔐 Secure password reset flow
- 📈 Performance monitoring endpoints

    
## 🛠️ Tech Stack

| Component           | Technology                          |
|---------------------|-------------------------------------|
| Framework           | Spring Boot 3.2                     |
| Database            | MySQL 8 (RDS compatible)            |
| ORM                 | Hibernate + JPA                     |
| Authentication      | Spring Security + JWT               |
| Real-time           | WebSocket (STOMP protocol)          |
| AI Integration      | Python Flask microservice           |
| Caching             | Redis                               |
| API Documentation   | Swagger UI + OpenAPI 3.0            |
| Testing             | JUnit 5, Mockito, TestContainers    |
| Build Tool          | Maven                               |

## 🚀 Quick Start

```bash
git clone https://github.com/elyes2dev/hackatonnet.git
cd hackatonnet

# Configure database in:
nano src/main/resources/application.yml

mvn spring-boot:run

