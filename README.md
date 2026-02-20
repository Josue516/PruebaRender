# IeoDomotics Web App

Aplicación web desarrollada con Spring Boot que permite la gestión de productos domóticos, categorías y pagos mediante PayPal.

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- Docker
- Render (Deploy)
- Railway (Base de datos)
- PayPal API

---

## Arquitectura del proyecto

Frontend + Backend desplegado en Render  
Base de datos MySQL alojada en Railway  
Pagos procesados mediante PayPal (Sandbox)

---

## ⚙️ Configuración del entorno

La aplicación utiliza variables de entorno para las credenciales:

### Base de datos

- MYSQLHOST
- MYSQLPORT
- MYSQLDATABASE
- MYSQLUSER
- MYSQLPASSWORD

### PayPal

- PAYPAL_CLIENT_ID
- PAYPAL_CLIENT_SECRET

---

## 🐳 Deploy en Render

El proyecto utiliza Docker para su despliegue.

Build y ejecución se realizan automáticamente mediante el Dockerfile incluido.

---

## Construcción local

Para compilar el proyecto: mvn clean package -DskipTests

Para ejecutar: java -jar target/*.jar

---

## Buenas prácticas implementadas

- Uso de variables de entorno para seguridad
- Eliminación de credenciales del repositorio
- Configuración de .gitignore
- Eliminación de archivos del IDE (.metadata)
- Configuración correcta de Lombok para producción
- Conexión remota segura a MySQL (Railway)

---

## 🌐 Demo

**Aplicación en producción:**  
[https://IeoDomotics.com](https://pruebarender-530n.onrender.com/)
