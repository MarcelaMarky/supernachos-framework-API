-- ---------------------------------------------------------------
-- Datos de ejemplo para probar la aplicacion.
-- Ejecutar en MySQL despues de haber creado la base supernachos.
-- ---------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS supernachos
	DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE supernachos;

-- Las tablas las crea Hibernate al arrancar la aplicacion
-- (spring.jpa.hibernate.ddl-auto=update), por eso aqui solo se insertan datos.

INSERT INTO producto (nombre, categoria, precio, stock) VALUES
	('Nachos con queso', 'Snacks', 6500.00, 40),
	('Gaseosa 400 ml', 'Bebidas', 3200.00, 60),
	('Salsa picante', 'Salsas', 4800.00, 25),
	('Guacamole personal', 'Salsas', 5200.00, 18),
	('Combo nachos + gaseosa', 'Combos', 9000.00, 15);

INSERT INTO cliente (nombre, telefono, direccion) VALUES
	('Ana Ruiz', '3001234567', 'Calle 10 # 5-20'),
	('Luis Gomez', '3109876543', 'Carrera 7 # 45-11'),
	('Marta Diaz', '3204455667', 'Avenida 3 # 12-30');

INSERT INTO empleado (nombre, cargo, telefono, correo) VALUES
	('Carlos Perez', 'Cajero', '3012223344', 'carlos.perez@supernachos.com'),
	('Laura Mesa', 'Administradora', '3025556677', 'laura.mesa@supernachos.com');
