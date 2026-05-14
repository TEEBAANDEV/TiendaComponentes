![header](https://capsule-render.vercel.app/api?type=transparent&fontColor=ffffff&text=Tienda%20Componentes)

Este repositorio centraliza la integración de los microservicios para la plataforma de gestión de ventas de componentes de hardware. El sistema utiliza una arquitectura distribuida basada en **Spring Boot**, orientada a la escalabilidad y al desacoplamiento de procesos.
## 📊 Estado del Proyecto
Actualmente el proyecto se encuentra en una fase media de integración.

**Progreso General:**
![Progress](https://geps.dev/progress/60?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Producto:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Usuario:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Inventario:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Carrito:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Ventas:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Recibo:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Reportes:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Envios:**
![Progress](https://geps.dev/progress/80?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Analiticas:**
![Progress](https://geps.dev/progress/40?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Pagos:**
![Progress](https://geps.dev/progress/40?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)
## 🚀 Arquitectura del Sistema

El proyecto se compone de los siguientes microservicios. Haz clic en cada uno para acceder a su rama correspondiente:

* **🛒 [TiendaComponentes-Carrito](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Carrito):** Gestión del carrito de compras por usuario.
* **💻 [TiendaComponentes-Producto](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Producto):** Catálogo central de productos y especificaciones.
* **👤 [TiendaComponentes-Usuario](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Usuario):** Manejo de perfiles, autenticación y seguridad (JWT).
* **💰 [TiendaComponentes-Ventas](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Ventas):** Orquestación del proceso de pago y facturación.
* **📦 [TiendaComponentes-Inventario](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Inventario):** Control de stock físico y disponibilidad.
* **🚚 [TiendaComponentes-Envio](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Envio):** Gestión logística y seguimiento de despachos.
* **📄 [TiendaComponentes-Recibo](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Recibo):** Generación de comprobantes y documentos tributarios.
* **📜 [TiendaComponentes-Reportes](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Reportes):** Generación de informes administrativos, métricas de rendimiento comercial y resúmenes históricos de inventario.
* **📈[TiendaComponentes-Analiticas](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Analitica):** Recolección y procesamiento de eventos en tiempo real para el análisis de tendencias de consumo y comportamiento de usuarios.
* **💵 [TiendaComponentes-Pagos](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Pagos):** Gestión de transacciones financieras, integración con pasarelas de pago y validación de estados de pago de las órdenes.

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java 21+
* **Framework Principal:** Spring Boot 3.x
* **Persistencia:** Spring Data JPA / Hibernate
* **Bases de Datos:** PostgreSQL / MySQL (Configurable vía application.properties)
* **Migraciones:** Flyway
* **Seguridad:** Spring Security & JWT
* **Comunicación:** WebClient (Arquitectura reactiva para interoperabilidad entre servicios)
* **Gestor de Dependencias:** Maven

## 📋 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- Java JDK 21 o superior.
- Maven 3.8+.
- IntelliJ IDEA

## 🔧 Configuración e Instalación

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/teebaandev/tiendacomponentes.git
    cd tiendacomponentes
    ```

2.  **Configurar variables de entorno:**
    Cada microservicio contiene un archivo `application.properties`. Asegúrate de actualizar las credenciales de la base de datos y las URLs de los servicios relacionados.

### **OPCIONAL/RECOMENDADO:**
Utilizar opcion de clonar mediante IntelliJ IDEA, para crear directorios de acuerdo a los branches. **Por ejemplo:**
```bash
cd tiendacomponentes/producto
cd tiendacomponentes/carrito
```

## 📡 Endpoints Principales (Resumen)

| Servicio   | Puerto (Defecto) | Funcionalidad Clave                                         |
|:-----------|:-----------------|:------------------------------------------------------------|
 | Producto   | 9090             | `api/v1/productos`                                          |                
| Inventario | 9091             | `/api/v1/inventario`                                        |
| Usuario    | 9092             | `/api/v1/auth`,`/api/v1/auth/register`,`/api/v1/auth/login` |
| Carrito    | 9093             | `/api/carrito`                                              |
| Ventas     | 9094             | `/api/v1/ventas`,`/api/v1/ventas/comprar/{idCarrito}`       |
| Recibo     | 9095             | `/api/v1/recibo`,`/api/v1/recibo/generar/{idVenta}`         |  
| Envios     | 9096             | `En progreso`                                               |  
| Analitica  | 9097             | `En progreso`                                               |  
| Reportes   | 9098             | `/api/v1/reportes` `/api/reportes/generar/{idRecibo}`       |  
| Pagos      | 9099             | `En progreso`                                               |  

***Este proyecto es de carácter educativo y privado. No se otorga permiso para su uso o distribución***
