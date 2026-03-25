# 📦 Digital Money House
 
> _Billetera Digital desarrollada con arquitectura de microservicios para gestionar usuarios, cuentas, transferencias y tarjetas._

---
## 🛠️ Tecnologías Utilizadas

### Backend
- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security
- JWT Authentication

### Arquitectura de Microservicios
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- OpenFeign

### Base de Datos
- MySQL

### Testing
- JUnit 5
- Mockito
- RestAssured
- Integration Testing

### Documentación API
- OpenAPI / Swagger

### DevOps
- Docker
- Docker Compose

### Control de versiones
- Git
- GitHub

---
## 📦 Microservicios del sistema

| Servicio | Responsabilidad |
|--------|--------|
| user-service | Gestión de usuarios |
| auth-service | Autenticación y JWT |
| account-service | Gestión de cuentas y CVU |
| gateway | Entrada única al sistema |
| eureka-server | Service discovery |
| config-server | Configuración centralizada |
---

## 📐 Arquitectura del Sistema

Digital Money House está construido siguiendo una **arquitectura de microservicios** donde cada servicio es independiente y se comunica a través de HTTP utilizando **Spring Cloud Gateway** y **OpenFeign**.
El sistema utiliza **Eureka Server** para el descubrimiento de servicios y **Config Server** para la centralización de configuraciones.

**Diagrama general :**

```
                ┌──────────────────┐
                │   API Gateway    │
                │     (gateway)    │
                └─────────┬────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│  user-service │ │ auth-service  │ │account-service│
└───────┬───────┘ └───────┬───────┘ └───────┬───────┘
        │                 │                 │
        └───────────────┬───────────────────┘
                        │
                 ┌───────────────┐
                 │ Eureka Server │
                 └───────────────┘

                 ┌───────────────┐
                 │ Config Server │
                 └───────────────┘
```


## 🔎 Microservicios

### 👤 User Service
Responsable de la **gestión de usuarios**:

- Registro de usuarios
- Verificación de email
- Consulta de información del usuario
- Orquestación del registro con otros servicios mediante comunicación entre microservicios

---

### 🔐 Auth Service
Encargado de la **autenticación y seguridad**:

- Login de usuarios
- Validación de credenciales
- Generación de **JWT Token**
- Manejo de autenticación para el acceso a la plataforma

---

### 💳 Account Service
Gestiona las **cuentas virtuales de los usuarios**:

- Creación automática de cuentas al registrarse un usuario
- Generación de **CVU**
- Generación de **Alias**

---

### 🚪 API Gateway
Punto de **entrada único al sistema**:

- Enrutamiento hacia los microservicios
- Centralización del acceso a las APIs
- Manejo de seguridad y filtros

---

### 📡 Eureka Server
Servidor de **Service Discovery** que permite que los microservicios se registren y puedan encontrarse dinámicamente dentro de la arquitectura.

---

### ⚙️ Config Server
Centraliza la **configuración de todos los microservicios**, permitiendo manejar propiedades y configuraciones externas de manera consistente.

---

## 🚀 Guía de Instalación y Ejecución

1. **Clonar el repositorio**
   ```
   git clone https://github.com/florenciabravo/digital-money-house.git
   cd digital-money-house
   ```
2. **Configuración del Config Server**

   > La configuración está externalizada para permitir cambios sin necesidad de redeployar los microservicios.

   Este proyecto utiliza un repositorio externo para centralizar configuraciones:

     👉 https://github.com/florenciabravo/config-server

     El **Config Server** obtiene las configuraciones desde este repositorio al iniciar.

   > ⚠️ Asegurarse de que el `config-server` tenga configurado correctamente el repositorio Git:

   ```yaml
      spring:
        cloud:
          config:
            server:
              git:
                uri: https://github.com/florenciabravo/config-server
   ```

3. **Configurar variables de entorno**
   > _Copiar `.env.example` a `.env` y completar los datos necesarios._

4. **Levantar base de datos y microservicios**
   
   ```
   docker-compose up --build
   ```

5. **Acceder a la documentación de la API**
    - Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 
---

## 🛣️ Principales Endpoints

> Por cada microservicio, algunos endpoints relevantes con ejemplos.

### Users Service

#### `POST /users/register`
Registrar nuevo usuario.

**Request**
```json
{
  "firstName": "Flor",
  "lastName": "Bravo",
  "email": "florenciabravonovillo@gmail.com",
  "password": "123456"
}
```

**Response - 201 Created**
```json
{
  "userId": 27,
  "email": "florenciabravonovillo@gmail.com",
  "cvu": "9841180115551712281989",
  "alias": "campo.cielo.pampa"
}
```
**Errores posibles**

| Status | Descripción |
|--------|-------------|
| 400 Bad Request | Datos inválidos o campos faltantes |
| 500 Internal Server Error | Error inesperado del servidor |
| 503 Service Unavailable | Servicio dependiente no disponible |
---
#### `POST /users/logout`
Logout y agregar token a blacklist.

**Request**
```
Auth Type: Bearer Token
Token: eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOjI3LCJzdWIiOiJmbG9yZW5jaWFicmF2b25vdmlsbG9AZ21haWwuY29tIiwiaWF0IjoxNzczNjg4ODE4LCJleHAiOjE3NzM2OTI0MTh9.as48tYf8HsKjcDjiSRrbmIToH-rQZCEn-FHpKiFEZm4
```

**Response - 200 OK**
```
Logout successful
```
**Errores posibles**

| Status                    | Descripción |
|---------------------------|-------------|
| 401 Unauthorized                      | No autorizado |
| 500 Internal Server Error | Error inesperado del servidor |

---

### Auth Service

#### `POST /auth/verify-email`
Verificar con el código de verificación recibido por email, solo una unica vez antes de loguearse.

**Request**
```json
{
  "email": "florenciabravonovillo@gmail.com",
  "code": "533117"
}
```

**Response - 200 OK**
```
Email verified successfully
```
**Errores posibles**

| Status | Descripción |
|------|-------------|
| 400 Bad Request | Código inválido |
| 404 Not Found | Usuario no encontrado |
| 500 Internal Server Error | Error inesperado del servidor |


---
#### `POST /auth/login`
Iniciar session.

**Request**
```json
{
  "email": "florenciabravonovillo@gmail.com",
  "password": "123456"
}
```

**Response - 200 OK**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOjI3LCJzdWIiOiJmbG9yZW5jaWFicmF2b25vdmlsbG9AZ21haWwuY29tIiwiaWF0IjoxNzczNjg4ODE4LCJleHAiOjE3NzM2OTI0MTh9.as48tYf8HsKjcDjiSRrbmIToH-rQZCEn-FHpKiFEZm4"
}
```
**Errores posibles**

| Status | Descripción                   |
|--------|-------------------------------|
| 400 Bad Request | Credenciales invalidas        |
| 404 Not Found | Usuario Inexistente           |
| 500 Internal Server Error | Error inesperado del servidor |
---

#### `POST /auth/forgot-password`
Generar token de recuperacion de password via email.

**Request**
```json
{
  "email": "florenciabravonovillo@gmail.com"
}
```

**Response - 200 OK**
```
If the email exists, a recovery link was sent
```
**Errores posibles**

| Status | Descripción                   |
|--------|-------------------------------|
| 400 Bad Request | Credenciales invalidas        |
| 500 Internal Server Error | Error inesperado del servidor |
---

#### `POST /auth/reset-password`
Generar token de recuperacion de password via email.

**Request**
```json
{
  "token": "03aefdb3-4e36-4328-bcfc-5d6b1571ca73",
  "newPassword": "Nueva123!",
  "repeatPassword": "Nueva123!"
}
```

**Response - 200 OK**
```
Password updated successfully
```
**Errores posibles**

| Status | Descripción                   |
|--------|-------------------------------|
| 400 Bad Request | Credenciales invalidas        |
| 500 Internal Server Error | Error inesperado del servidor |

---

## 🧪 Testing
La estrategia de testing del proyecto combina **testing manual, exploratorio y automatizado**.

### 📬 Postman Collection
  Colección de endpoints para validar el flujo principal de la aplicación.

  > [Digital Money House - Sprint 1.postman_collection.json](/docs/Digital-Money-House-Sprint-1.postman_collection.json)

Una vez importada en Postman, ejecutar los requests en el siguiente orden:

1. **Register User**
2. **Verify Email**
3. **Login**
4. **Logout**
5. **Forgot Password**
6. **Reset Password**

---

### 📋 Documentación de Testing

- **Testing Kickoff**
  > [Testing-Kickoff.pdf](/docs/Testing-Kickoff.pdf)

- **Testing Exploratorio**
  > [Testing-Exploratorio.pdf](/docs/Testing-Exploratorio.pdf)


- **Plan de Casos de Prueba**
  > [Plan-de-Casos-de-Prueba-DMH-Sprint-1.pdf](/docs/Plan-de-Casos-de-Prueba-DMH-Sprint-1.pdf)


- **Ejecución de Casos de Prueba**
  > [Ejecucion-de-Casos-de-Prueba-Sprint-1.pdf](/docs/Ejecucion-de-Casos-de-Prueba-Sprint-1.pdf)
---


### 🤖 Tests Automatizados
Los microservicios incluyen **tests unitarios y de integración** implementados con **JUnit y Spring Boot Test**.
  > _Para ejecutar los test: ( `mvn test`)._

Los tests validan principalmente:

- Registro de usuario
- Validación de email existente
- Login de usuario
- Integración entre User Service y Account Service

---

### 🧪 Testing Manual
  
Para probar el sistema manualmente:

1. **Levantar la infraestructura del sistema:**
   - Eureka Server
   - Config Server
   - API Gateway
   - Microservicios (Auth Service, User Service y Acccount Service)
2. **Importar la colección de Postman incluida en el repositorio.**
3. **Ejecutar el flujo principal de usuario:**
   - Registrar un usuario
   - Verificar el email
   - Iniciar sesión

Esto permite validar el flujo completo de creación y autenticación de usuarios dentro de la arquitectura de microservicios.

---

## 💡 Decisiones Técnicas y Problemas Resueltos

### Autenticación con JWT
Se eligió **JWT (JSON Web Token)** para manejar autenticación stateless entre microservicios.  
Esto permite que cada request incluya el token sin necesidad de mantener sesiones en el servidor.

### Arquitectura de Microservicios
El sistema se separó en múltiples servicios independientes para mejorar:

- Escalabilidad
- Mantenibilidad
- Despliegue independiente

### Service Discovery con Eureka
Se implementó **Eureka Server** para permitir que los microservicios se registren dinámicamente y puedan encontrarse sin depender de direcciones fijas.

### API Gateway
Se utiliza **Spring Cloud Gateway** como punto de entrada único al sistema para:

- Centralizar el acceso a los servicios
- Manejar filtros de seguridad
- Simplificar el consumo de APIs.

### Comunicación entre Microservicios
Se utiliza **OpenFeign** para la comunicación declarativa entre servicios, permitiendo una integración más simple y mantenible.

---

## 👨‍💻 Desarrollado por

- Florencia Bravo ✨ - Linkedin https://www.linkedin.com/in/florencia-bravo-novillo/
-  Proyecto Final - Software Developer Año: 2026

---

## 📚 Glosario

- **CVU:** Clave Virtual Uniforme, identificador de cuentas en billeteras digitales.
- **Alias:** Identificador alternativo de cuenta.
- **Endpoint:** URL pública de una función del sistema.
- **JWT:** JSON Web Token, método de autenticación.