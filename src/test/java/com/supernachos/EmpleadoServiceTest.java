package com.supernachos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.supernachos.excepcion.OperacionInvalidaException;
import com.supernachos.excepcion.RecursoNoEncontradoException;
import com.supernachos.formulario.LineaVentaForm;
import com.supernachos.formulario.VentaForm;
import com.supernachos.modelo.Cliente;
import com.supernachos.modelo.Empleado;
import com.supernachos.modelo.Producto;
import com.supernachos.servicio.ClienteService;
import com.supernachos.servicio.EmpleadoService;
import com.supernachos.servicio.ProductoService;
import com.supernachos.servicio.VentaService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/** Pruebas del modulo de empleados. Cada prueba se revierte al terminar. */
@SpringBootTest
@Transactional
class EmpleadoServiceTest {

	@Autowired
	private EmpleadoService empleadoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ProductoService productoService;

	@Autowired
	private VentaService ventaService;

	@Autowired
	private Validator validator;

	@Test
	void guardarAsignaIdYPermiteConsultarElEmpleado() {
		Empleado guardado = empleadoService.guardar(empleado("Carlos Perez", "Cajero", "carlos@supernachos.com"));

		assertThat(guardado.getIdEmpleado()).isNotNull();
		Empleado consultado = empleadoService.buscarPorId(guardado.getIdEmpleado());
		assertThat(consultado.getNombre()).isEqualTo("Carlos Perez");
		assertThat(consultado.getCargo()).isEqualTo("Cajero");
		assertThat(consultado.getCorreo()).isEqualTo("carlos@supernachos.com");
	}

	@Test
	void editarActualizaElCargoDelEmpleado() {
		Empleado guardado = empleadoService.guardar(empleado("Laura Gil", "Cajera", null));

		guardado.setCargo("Supervisora");
		empleadoService.guardar(guardado);

		assertThat(empleadoService.buscarPorId(guardado.getIdEmpleado()).getCargo()).isEqualTo("Supervisora");
	}

	@Test
	void buscarFiltraPorNombreOCargoSinImportarMayusculas() {
		empleadoService.guardar(empleado("Andres Mora", "BodegueroPrueba", null));
		empleadoService.guardar(empleado("Sofia Rios", "BodegueroPrueba", null));
		empleadoService.guardar(empleado("Julian Prueba Castro", "Cajero", null));

		List<String> porCargo = empleadoService.listar("bodegueroprueba").stream().map(Empleado::getNombre).toList();
		List<String> porNombre = empleadoService.listar("PRUEBA CASTRO").stream().map(Empleado::getNombre).toList();

		assertThat(porCargo).containsExactly("Andres Mora", "Sofia Rios");
		assertThat(porNombre).containsExactly("Julian Prueba Castro");
	}

	@Test
	void buscarUnIdQueNoExisteLanzaExcepcion() {
		assertThatThrownBy(() -> empleadoService.buscarPorId(999999))
				.isInstanceOf(RecursoNoEncontradoException.class)
				.hasMessageContaining("No existe el empleado");
	}

	@Test
	void eliminarEmpleadoSinVentasLoBorra() {
		Empleado guardado = empleadoService.guardar(empleado("Empleado temporal", "Auxiliar", null));

		empleadoService.eliminar(guardado.getIdEmpleado());

		assertThatThrownBy(() -> empleadoService.buscarPorId(guardado.getIdEmpleado()))
				.isInstanceOf(RecursoNoEncontradoException.class);
	}

	@Test
	void noPermiteEliminarUnEmpleadoConVentas() {
		Empleado vendedor = empleadoService.guardar(empleado("Empleado con ventas", "Cajero", null));
		registrarVentaDe(vendedor);

		assertThatThrownBy(() -> empleadoService.eliminar(vendedor.getIdEmpleado()))
				.isInstanceOf(OperacionInvalidaException.class)
				.hasMessageContaining("tiene ventas registradas");
	}

	@Test
	void validaCamposObligatoriosYFormatoDelCorreo() {
		Empleado invalido = empleado("", "", "correo-sin-arroba");

		List<String> camposConError = validator.validate(invalido).stream()
				.map(ConstraintViolation::getPropertyPath).map(Object::toString).toList();

		assertThat(camposConError).containsExactlyInAnyOrder("nombre", "cargo", "correo");
	}

	private Empleado empleado(String nombre, String cargo, String correo) {
		Empleado empleado = new Empleado();
		empleado.setNombre(nombre);
		empleado.setCargo(cargo);
		empleado.setCorreo(correo);
		return empleado;
	}

	private void registrarVentaDe(Empleado empleado) {
		Producto producto = new Producto();
		producto.setNombre("Producto de prueba");
		producto.setCategoria("Varios");
		producto.setPrecio(new BigDecimal("1000.00"));
		producto.setStock(10);
		producto = productoService.guardar(producto);

		Cliente cliente = new Cliente();
		cliente.setNombre("Cliente de prueba");
		cliente = clienteService.guardar(cliente);

		VentaForm form = new VentaForm();
		form.setIdCliente(cliente.getIdCliente());
		form.setIdEmpleado(empleado.getIdEmpleado());
		LineaVentaForm linea = form.getLineas().get(0);
		linea.setIdProducto(producto.getIdProducto());
		linea.setCantidad(1);
		ventaService.registrar(form);
	}

}
