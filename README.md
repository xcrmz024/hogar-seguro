# HogarSeguro - Sistema de Gestión Integral para Santuario Animal


**HogarSeguro** es una plataforma web diseñada para la gestión integral de un santuario animal.

El sistema permite administrar animales rescatados, gestionar procesos de adopción, amadrinamiento y voluntariado, además de registrar donaciones y manejar solicitudes mediante un panel administrativo.


## 🌐 Live demo


https://hogar-seguro-production.up.railway.app/

## 🔧 Stack Tecnológico


**Backend**
- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring Security

**Frontend**
- Thymeleaf
- HTML5
- CSS3
- Bootstrap 5

**Base de Datos**
- MySQL 8

**Seguridad**
- Autenticación stateless con **JWT**
- Cifrado de contraseñas con **BCrypt**

**Testing**
- JUnit 5
- Mockito
- H2 Database (tests de integración)

**Build & Dependencias**
- Maven

**Infraestructura**
- Docker
- Docker Compose
- Railway (PaaS)


## ✨ Características principales


- **Panel administrativo:** (CRUD para la gestión del santuario) protegido con autenticación y autorización mediante Spring Security.
- **Autenticación** basada en JWT almacenado en cookies HttpOnly.
- **Gestión Dinámica de Habitantes:** Carga automatizada de perfiles desde la
  base de datos.
- **Sistema de Solicitudes:** Formulario inteligente con Bean Validation para Adopciones,
  Amadrinamientos y Voluntariados.
- **Portal de Donaciones:** Interfaz simulada para el registro y gestión de apoyo
  económico simulado.
- **Diseño Responsivo:** Interfaz optimizada para móviles y escritorio usando
  Bootstrap.
- Arquitectura en capas/ patrón **MVC**.



## 🐳 Instalación y Ejecución con Docker (local)


Para ejecutar este proyecto localmente sin necesidad de configurar una base de datos manual, asegúrate de tener instalado **Docker Desktop**.

1. **Clonar este repositorio:**
   ```bash
   git clone https://github.com/xcrmz024/hogar-seguro
   cd repo_name
   ```
   
2. **Configurar variables de entorno:**
   
    Crea un archivo `.env` en la raíz con tus credenciales

   ```env
   # Database
   DB_HOST=db
   DB_NAME=hogar_seguro_db
   DB_USER=root
   DB_PASSWORD=your_password

   # JWT
   JWT_SECRET_KEY=your_secret_key_min_32_chars
   ```

3. **Ejcutar con Docker Compose:**

   `docker compose up --build`


4. **La aplicación estará disponible en:** 
   http://localhost:8080


### Demo (credenciales)

**Admin access (admin panel)**

 - usernname: admin
 - password: admin123


*Estas credenciales son sólo para uso demostrativo.*
    


## 📝 Disclaimer


Este proyecto fue desarrollado con fines académicos y de portafolio. Los datos, nombres y fotografías de los animales son ficticios y utilizados para demostración técnica. 
Inspirado en la labor real de un santuario animal.

---
**Desarrollado por [xcrmz024](https://github.com/xcrmz024)**