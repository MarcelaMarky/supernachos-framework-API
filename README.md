# Super Nachos Market

Aplicación web para la gestión de un supermercado, desarrollada con **Spring Boot**, **Spring Data JPA**, **Thymeleaf** y **MySQL**.

## Tecnologías

* Java 17
* Spring Boot
* Spring Data JPA
* Thymeleaf
* MySQL
* Maven

## Funcionalidades

* Panel principal con indicadores.
* Gestión de productos: registrar, consultar, editar y eliminar.
* Gestión de clientes: registrar, consultar, editar y eliminar.
* Gestión de empleados: registrar, consultar, editar y eliminar.
* Registro y consulta de ventas.
* Anulación de ventas.
* Control y actualización automática del stock.
* Validaciones de datos y reglas de negocio.

## Iniciar la aplicación

1. Tener MySQL ejecutándose y la base de datos `supernachos` creada.
2. Verificar las credenciales de conexión en:

`src/main/resources/application.properties`

3. Desde la carpeta del proyecto ejecutar:

```bash
.\mvnw.cmd spring-boot:run
```

4. Abrir en el navegador:

`http://localhost:8080/`

## Pruebas

El proyecto incluye pruebas automatizadas con **H2 en memoria**.

Para ejecutarlas:

```bash
.\mvnw.cmd test
```
