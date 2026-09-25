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
* Inicio de sesión y API REST de autenticación.

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

## Inicio de sesión

Al abrir la aplicación se muestra la pantalla de login. El usuario inicial se crea automáticamente la primera vez que arranca la aplicación:

| Usuario | Clave |
|---|---|
| `admin` | `1234` |

Los usuarios se guardan en la tabla `usuario` de MySQL. Para cerrar sesión se usa el enlace **Cerrar sesion** del menú.

## API

La API responde en JSON y usa la misma sesión que la aplicación web.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/login` | Inicia sesión |
| GET | `/api/sesion` | Muestra el usuario con sesión activa |
| POST | `/api/logout` | Cierra la sesión |

Ejemplo de inicio de sesión (Postman o curl):

```json
POST http://localhost:8080/api/login
{ "usuario": "admin", "clave": "1234" }
```

Respuesta:

```json
{ "mensaje": "Inicio de sesion correcto", "usuario": "admin" }
```

Si el usuario o la clave son incorrectos, o si se llama a la API sin iniciar sesión, se responde con el código **401**.

## Pruebas

El proyecto incluye pruebas automatizadas con **H2 en memoria**.

Para ejecutarlas:

```bash
.\mvnw.cmd test
```
