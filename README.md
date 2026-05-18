![header](https://capsule-render.vercel.app/api?type=transparent&fontColor=ffffff&text=Tienda%20Componentes)

Este repositorio centraliza la integración de los microservicios para la plataforma de gestión de ventas de componentes de hardware. El sistema utiliza una arquitectura distribuida basada en **Spring Boot**, orientada a la escalabilidad y al desacoplamiento de procesos.
## 📌 Índice
* [📊 Estado del Proyecto](#-estado-del-proyecto)
* [🚀 Arquitectura del Sistema](#-arquitectura-del-sistema)
* [🛠️ Tecnologías Utilizadas](#️-tecnologías-utilizadas)
* [📋 Requisitos Previos](#-requisitos-previos)
* [🔧 Configuración e Instalación](#-configuración-e-instalación)
* [📡 Endpoints Principales (Resumen)](#-endpoints-principales-resumen)
* [📬 Instrucciones Postman](#instrucciones-postman)
    * [📦 Producto](#producto)
    * [🏢 Inventario](#inventario)
    * [👤 Usuario](#usuario)
    * [🛒 Carrito](#carrito)
    * [💰 Ventas](#ventas)
    * [📄 Recibo](#recibo)
    * [🚚 Envíos](#envios)
    * [💬 Comentarios](#comentarios)
    * [📊 Reportes](#reportes)
    * [⭐ Wishlist](#wishlist)

## 📊 Estado del Proyecto
Actualmente el proyecto se encuentra en una fase media de integración.

**Progreso General:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

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
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Reseñas:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)

**Lista de deseados:**
![Progress](https://geps.dev/progress/100?dangerColor=ff4b2b&warningColor=ffa000&successColor=2ecc71)
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
* **📈[TiendaComponentes-Comentarios](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Comentarios):** Gestión, publicación y moderación de reseñas de productos hechas por los usuarios.
* **💵 [TiendaComponentes-Wishlist](https://github.com/TEEBAANDEV/TiendaComponentes/tree/Wishlist):** Gestión de la lista de deseados de la tienda, permitiendo a los usuarios guardar productos para compras futuras.

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

1. **Crear directorio general para los microservicios**
    ```bash
    mkdir TiendaComponentes
    ```
2. **Clonar por Branches:**
    ```bash
    cd tiendacomponentes
   git clone -b Producto --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Productos
   git clone -b Inventario --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Inventario
   git clone -b Usuario --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Usuario
   git clone -b Carrito --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Carrito
   git clone -b Ventas --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Ventas
   git clone -b Recibo --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Recibo
   git clone -b Reportes --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Reportes
   git clone -b Envio --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Envios
   git clone -b Comentarios --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Comentarios
   git clone -b Wishlist --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Wishlist
    ```

3. **Configurar variables de entorno:**
    Cada microservicio contiene un archivo `application.properties`. Asegúrate de actualizar las credenciales de la base de datos y las URLs de los servicios relacionados.

## 📡 Endpoints Principales (Resumen)

| Servicio    | Puerto (Defecto) | Funcionalidad Clave                                                                           |
|:------------|:-----------------|:----------------------------------------------------------------------------------------------|
 | Producto    | 9090             | `/api/v1/productos`                                                                           |                
| Inventario  | 9091             | `/api/v1/inventario`                                                                          |
| Usuario     | 9092             | `/api/v1/auth`                                                                                |
| Carrito     | 9093             | `/api/v1/carrito`                                                                             |
| Ventas      | 9094             | `/api/v1/ventas`                                                                              |
| Recibo      | 9095             | `/api/v1/recibo`                                                                              |  
| Envios      | 9096             | `/api/v1/envio`                                                                               |  
| Comentarios | 9097             | `/api/v1/resena`                                                                              |  
| Reportes    | 9098             | `/api/v1/reportes`                                                                            |  
| Wishlist    | 9099             | `/api/v1/wishlist`                                                                            |  

# 📬INSTRUCCIONES POSTMAN

## 📦PRODUCTO
### GET
Para mostrar todo el listado es simplemente
```bash
/api/v1/productos
```
Para buscar por un producto en especifico
```bash
/api/v1/productos/{IdProducto}
```
### POST
```bash
{
    "nombre" : "Nombre producto",
    "descripcion" : "Descripcion producto",
    "precio" : 0.0 (Precio producto)
}
```
### PUT
En este caso es el mismo formato que en el POST
```bash
{
    "nombre" : "Nombre producto",
    "descripcion" : "Descripcion producto",
    "precio" : 0.0 (Precio producto)
}
```
### DELETE
Este funciona igual que en la busqueda de un producto
```bash
/api/v1/productos/{IdProducto}
```
## 🏢INVENTARIO

### GET
Para mostrar todo el inventario es igual que en el microservicio de producto
```bash
/api/v1/inventario
```
La busqueda es por producto, asi que es la misma id
```bash
/api/v1/inventario/{IdProducto}
```

### POST
El formato para publicar en inventario es de la siguiente
```bash
{
    "idProducto": 0 (Id producto),
    "cantidad": 0 (Asignar cantidad)
}
```

### PUT
Es de la misma forma que post
```bash
{
    "idProducto": 0 (Id producto),
    "cantidad": 0 (Asignar cantidad)
}
```

### DELETE
Esto es el descuento del producto, para que sea eliminado deberia estar en 0
```bash
/api/v1/inventario/descontar
```
```bash
{
  "idProducto" : "ID del producto",
  "cantidad" : "Cantidad que se descuenta"
}
```
## 👤USUARIO

### GET
En este caso solamente funciona por usuario, no tiene un listado general, para eso se podra ver en DB
```bash
api/v1/users/{IdUsuario}
```

### POST

```bash
{
    "username": "Nombre Usuario",
    "password": "Contraseña",
    "direccion": "Direccion",
    "role" : "Rol"
}
```
* **La modificacion y eliminacion del usuario seria mediante manipulacion manual por SQL**
## 🛒CARRITO

### GET
En este caso se veran todos los carritos de esta forma
```bash
/api/v1/carrito
```
Para filtrar el carrito, seria por el ID del usuario
```bash
/api/v1/carrito/usuario/{IdUsuario}
```
### POST
Primero se debe ingresar de esta forma en el postman
```bash
/api/v1/carrito/lote
```
Para agregar productos al carrito funciona de la siguiente forma
```bash
[
    {
        "idUsuario": {IdUsuario},
        "idProducto": {IdProducto},
        "cantidad": {Cantidad}
    }
]
```
Tambien se pueden agregar mas de 1 producto de la siguiente forma
```bash
[
    {
        "idUsuario": {IdUsuario},
        "idProducto": {IdProducto1},
        "cantidad": {Cantidad}
    },
    {
        "idUsuario": {IdUsuario},
        "idProducto": {IdProducto2},
        "cantidad": {Cantidad}
    },
    {
        "idUsuario": {IdUsuario},
        "idProducto": {IdProducto3},
        "cantidad": {Cantidad}
    }
]
```

### DELETE
Existen 2 formas de eliminar, una es por id del carrito
```bash
/api/v1/carrito/{IdCarrito}
```

La otra forma es un vaciado completo del carrito usando la ID del usuario
```bash
/api/v1/carrito/usuario/{IdUsuario}
```
## 💰VENTAS

### GET
Para obtener todas las ventas es mediante
```bash
/api/v1/Ventas
```
Para obtener por ID es
```bash
/api/v1/Ventas/{IdVenta}
```
### POST
Para generar una venta primero se necesita de un carrito existente asociado a un usuario, en caso de tener, la venta se genera de la siguiente forma
```bash
/api/v1/Ventas/comprar/{IdUsuario}
```
### PUT
En este caso solo se puede modificar el estado de la venta
```bash
{
    "id" : "ID de Venta"
    "nuevoEstado" : "Nuevo estado de venta"
}
```

### DELETE
Para el eliminado de la venta es mediante la propia ID de la venta
```bash
/api/v1/Ventas/{IdVenta}
```

## 📄RECIBO

### GET
Para ver los recibos es mediante esta forma
```bash
/api/v1/recibo
```
Tambien se pueden ver por su id (Por lo general tiene la misma id que ventas)
```bash
/api/v1/recibo/{IdRecibo}
```
### POST
Para generar recibos es de la misma forma que en ventas, necesita el id de ventas en este caso
```bash
/api/v1/recibo/generar/{IdVenta}
```
**La modificacion y la eliminacion de los recibos seria manual mediante SQL**
## 🚚ENVIOS
### GET
Para ver los envios es de la siguiente forma
```bash
/api/v1/envio
```
Para ver un envio en especifico es de la siguiente forma
```bash
/api/v1/envio/{IdEnvio}
```
### POST
Para generar un envio, primero debe haber algun recibo en DB, ya que se utiliza su id para generar
```bash
/api/v1/envio/generar/{IdRecibo}
```

* **La modificacion y eliminacion de los envios se realizan por medio de SQL**

## 💬COMENTARIOS

### GET
Para ver todos los comentarios es de la siguiente forma
```bash
/api/v1/resenas
```
Para ver comentarios por filtros es mediante la ID del producto, se vera el promedio de calificaciones de estrellas
```bash
/api/v1/resenas/{IdProducto}/promedio
```
### POST
Para generar comentarios, primero se debe utilizar el ID del producto
```bash
/api/v1/resenas/comentar/{IdProducto}
```
En Postman se debe ingresar de la siguiente forma
```bash
{
    "usuarioId": {IdUsuario},
    "productoId": {IdProducto},
    "calificacion": 1-5 (Estrellas),
    "comentario": "Comentario"
}
```
* **La modificacion y la eliminacion de los comentarios se realizan a traves de SQL**

## 📊REPORTES

### GET
Para ver los reportes es de la siguiente forma
```bash
/api/v1/reportes
```
Tambien se pueden ver por su ID
```bash
/api/v1/reportes/{IdReporte}
```
### POST
Para generar un reporte, primero debe haber un recibo existente
```bash
/api/v1/reportes/generar/{IdRecibo}
```

### DELETE
La eliminacion de los reportes es mediante su ID
```bash
/api/v1/reportes/{IdReporte}
```

## ⭐WISHLIST

### GET
Para ver los deseados es de la siguiente forma
```bash
/api/v1/wishlist
```
Para filtrar, es mediante la ID del usuario
```bash
/api/v1/wishlist/usuario/{Idusuario}
```
### POST
Primero se debe ingresar de esta forma en el postman
```bash
/api/v1/wishlist/agregar
```
Para agregar productos a la Wishlist funciona de la siguiente forma
```bash
[
    {
        "idUsuario": {IdUsuario},
        "idProducto": {IdProducto},
        "cantidad": {Cantidad}
    }
]
```
Tambien se pueden agregar mas de 1 producto de la siguiente forma
```bash
[
    {
        "idUsuario": {IdUsuario},
        "idProducto": {IdProducto1},
        "cantidad": {Cantidad}
    },
    {
        "idUsuario": {IdUsuario},
        "idProducto": {IdProducto2},
        "cantidad": {Cantidad}
    },
    {
        "idUsuario": {IdUsuario},
        "idProducto": {IdProducto3},
        "cantidad": {Cantidad}
    }
]
```
### DELETE
Existen 2 formas de eliminar, una es por id de la Wishlist
```bash
/api/v1/wishlist/{IdCarrito}
```

La otra forma es un vaciado completo de la Wishlist usando la ID del usuario
```bash
/api/v1/wishlist/usuario/{IdUsuario}
```
### Licencia
```
Copyright (c) 2026 TEEBAANDEV, ItsariMoreno, Shamo-CH

Todos los derechos reservados.

No se permite el uso, copia, modificación o distribución de este software y sus archivos de documentación asociados para ningún propósito, sin el permiso previo y por escrito del titular de los derechos de autor.
```
