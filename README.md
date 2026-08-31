# Super Nachos Market — version con frameworks

Aplicacion web para la gestion de productos, clientes, empleados y ventas de
Super Nachos Market. Es la evolucion del proyecto de servlets + JSP de la fase
anterior, ahora construida sobre **Spring Boot** con **Spring Data JPA** y
**Thymeleaf**.

## Tecnologias

| Capa | Herramienta |
| --- | --- |
| Framework backend | Spring Boot 4.1.1 |
| Acceso a datos | Spring Data JPA (Hibernate) |
| Vistas | Thymeleaf |
| Validaciones | Jakarta Bean Validation |
| Base de datos | MySQL 8 (base `supernachos`) |
| Servidor | Tomcat embebido |
| Construccion | Maven (wrapper incluido) |
| Pruebas | JUnit 5 + H2 en memoria |

## Requisitos

* JDK 17
* MySQL 8 en `localhost:3306` con la base de datos `supernachos`

## Configuracion

Las credenciales estan en `src/main/resources/application.properties`. Por
defecto usa `root` / `marky`; se pueden cambiar ahi o con las variables de
entorno `DB_USER` y `DB_PASSWORD`.

Hibernate crea o completa las tablas al arrancar
(`spring.jpa.hibernate.ddl-auto=update`), asi que basta con que la base
`supernachos` exista. Para cargar datos de prueba se puede ejecutar el archivo
`datos-ejemplo.sql`.

## Ejecutar

Desde esta carpeta, en Windows:

```text
.\mvnw.cmd spring-boot:run
```

Luego abrir <http://localhost:8080/>

## Pruebas

```text
.\mvnw.cmd test
```

Las pruebas corren contra una base H2 en memoria, por lo que no tocan los datos
reales de MySQL.

## Modulos

| Ruta | Descripcion |
| --- | --- |
| `/` | Panel con indicadores y ultimas ventas |
| `/productos` | CRUD de productos con busqueda y control de stock |
| `/clientes` | CRUD de clientes |
| `/empleados` | CRUD de empleados |
| `/ventas` | Registro, consulta y anulacion de ventas |

## Reglas de negocio implementadas

* Al registrar una venta el sistema toma el **precio actual** de cada producto,
  calcula subtotales y total, y **descuenta el stock**.
* No permite vender mas unidades de las disponibles.
* Al anular una venta las unidades **regresan al inventario**.
* No deja eliminar productos, clientes ni empleados que ya tengan ventas
  asociadas.

## Estructura del codigo

```text
src/main/java/com/supernachos
├── SupernachosApplication.java   punto de entrada
├── modelo/                       entidades JPA (Producto, Cliente, Empleado, Venta, DetalleVenta)
├── repositorio/                  interfaces Spring Data JPA
├── servicio/                     reglas de negocio y transacciones
├── controlador/                  controladores MVC y manejo de errores
├── formulario/                   objetos de formulario (VentaForm, LineaVentaForm)
└── excepcion/                    excepciones propias

src/main/resources
├── application.properties        configuracion
├── templates/                    vistas Thymeleaf
└── static/                       CSS y JavaScript
```

## Nota sobre el modulo de ventas

Una venta se **crea**, se **consulta** y se **anula**; no se edita, porque
modificar una venta ya registrada obligaria a recalcular el inventario de forma
retroactiva. Anular y volver a registrar deja el stock correcto y es el flujo
que usan los sistemas de punto de venta.
