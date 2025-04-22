# <img src="whatsapp-clone-ui/public/icon.png" alt="WhatsApp Clone Icon" width="32" height="32"> WhatsApp Clone 

## About
A real-time chat application built with Spring Boot and Angular, featuring WebSocket communication and Keycloak authentication.

## Features

- Real-time messaging using WebSocket
- User authentication with Keycloak
- File sharing (up to 50MB)
- User presence detection
- Message history
- Responsive UI with Angular Material
- REST API with OpenAPI documentation

## Tech Stack

### Backend (Spring Boot)
- Java 17
- Spring Boot 3.4.1
- Spring Security with OAuth2
- Spring WebSocket
- Spring Data JPA
- PostgreSQL
- Lombok
- OpenAPI/Swagger Documentation

### Frontend (Angular)
- Angular 19.1.0
- TypeScript
- SCSS
- SockJS & StompJS for WebSocket
- Keycloak JS Client
- Angular Material
- Bootstrap 5

### Authentication
- Keycloak 26.0.0

## Prerequisites

- Java 17 or higher
- Node.js and npm
- PostgreSQL
- Keycloak Server
- Maven

## Setup & Installation

### 1. Database Setup
```sql
CREATE DATABASE whatsapp_clone;
```

### 2. Keycloak Setup
1. Download and start Keycloak server
2. Create a new realm: `whatsapp-clone`
3. Configure client settings:
   - Client ID: `whatsapp-clone-app`
   - Access Type: `public`
   - Valid Redirect URIs: `http://localhost:4200/*`

### 3. Backend Setup
```bash
cd whatsappclone
mvn clean install
mvn spring-boot:run
```
The backend will start on `http://localhost:1234`

### 4. Frontend Setup
```bash
cd whatsapp-clone-ui
npm install
ng serve
```
The frontend will start on `http://localhost:4200`

## API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:1234/swagger-ui.html`
- OpenAPI JSON: `http://localhost:1234/v3/api-docs`

## Configuration

### Backend Configuration (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/whatsapp_clone
    username: your_username
    password: your_password
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/whatsapp-clone
```

### Frontend Configuration
Update the API and Keycloak configuration in `environment.ts`:
```typescript
export const environment = {
  apiUrl: 'http://localhost:1234',
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'whatsapp-clone',
    clientId: 'whatsapp-clone'
  }
};
```

## Features in Detail

1. **Real-time Messaging**
   - WebSocket integration for instant message delivery
   - Message status updates (sent, delivered, read)
   - Typing indicators

2. **Authentication & Security**
   - OAuth2/JWT-based authentication with Keycloak
   - Role-based access control
   - Secure WebSocket connections

3. **User Management**
   - User profile management
   - Online/offline status
   - User search functionality
   - User synchronization with Keycloak

4. **File Sharing**
   - Support for multiple file types
   - File size limit: 50MB
   - Secure file transfer

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Spring Boot team for the excellent framework
- Angular team for the powerful frontend framework
- Keycloak team for the robust authentication solution