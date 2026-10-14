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

/** Pruebas del modulo de clientes. Cada prueba se revierte al terminar. */
@SpringBootTest
@Transactional
class ClienteServiceTest {

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private EmpleadoService empleadoService;

	@Autowired
	private ProductoService productoService;

	@Autowired
	private VentaService ventaService;

	@Autowired
	private Validator validator;

	@Test
	void guardarAsignaIdYPermiteConsultarElCliente() {
		Cliente guardado = clienteService.guardar(cliente("Ana Ruiz", "3001234567", "Calle 10 # 5-20"));

		assertThat(guardado.getIdCliente()).isNotNull();
		Cliente consultado = clienteService.buscarPorId(guardado.getIdCliente());
		assertThat(consultado.getNombre()).isEqualTo("Ana Ruiz");
		assertThat(consultado.getTelefono()).isEqualTo("3001234567");
		assertThat(consultado.getDireccion()).isEqualTo("Calle 10 # 5-20");
	}

	@Test
	void editarActualizaLosDatosDelCliente() {
		Cliente guardado = clienteService.guardar(cliente("Luis Gomez", "3109876543", "Carrera 7 # 45-11"));

		guardado.setTelefono("3150000000");
		clienteService.guardar(guardado);

		assertThat(clienteService.buscarPorId(guardado.getIdCliente()).getTelefono()).isEqualTo("3150000000");
	}

	@Test
	void buscarFiltraPorNombreSinImportarMayusculas() {
		clienteService.guardar(cliente("Marta Diaz Prueba", null, null));
		clienteService.guardar(cliente("Mario Diaz Prueba", null, null));
		clienteService.guardar(cliente("Pedro Lopez Prueba", null, null));

		List<String> encontrados = clienteService.listar("DIAZ PRUEBA").stream().map(Cliente::getNombre).toList();

		assertThat(encontrados).containsExactly("Mario Diaz Prueba", "Marta Diaz Prueba");
	}

	@Test
	void buscarUnIdQueNoExisteLanzaExcepcion() {
		assertThatThrownBy(() -> clienteService.buscarPorId(999999))
				.isInstanceOf(RecursoNoEncontradoException.class)
				.hasMessageContaining("No existe el cliente");
	}

	@Test
	void eliminarClienteSinVentasLoBorra() {
		Cliente guardado = clienteService.guardar(cliente("Cliente temporal", null, null));

		clienteService.eliminar(guardado.getIdCliente());

		assertThatThrownBy(() -> clienteService.buscarPorId(guardado.getIdCliente()))
				.isInstanceOf(RecursoNoEncontradoException.class);
	}

	@Test
	void noPermiteEliminarUnClienteConVentas() {
		Cliente comprador = clienteService.guardar(cliente("Cliente con compras", null, null));
		registrarVentaPara(comprador);

		assertThatThrownBy(() -> clienteService.eliminar(comprador.getIdCliente()))
				.isInstanceOf(OperacionInvalidaException.class)
				.hasMessageContaining("tiene ventas registradas");
	}

	@Test
	void validaNombreObligatorioYLongitudDeTelefono() {
		Cliente invalido = cliente(" ", "123456789012345678901", null);

		List<String> camposConError = validator.validate(invalido).stream()
				.map(ConstraintViolation::getPropertyPath).map(Object::toString).toList();

		assertThat(camposConError).containsExactlyInAnyOrder("nombre", "telefono");
	}

	private Cliente cliente(String nombre, String telefono, String direccion) {
		Cliente cliente = new Cliente();
		cliente.setNombre(nombre);
		cliente.setTelefono(telefono);
		cliente.setDireccion(direccion);
		return cliente;
	}

	private void registrarVentaPara(Cliente cliente) {
		Producto producto = new Producto();
		producto.setNombre("Producto de prueba");
		producto.setCategoria("Varios");
		producto.setPrecio(new BigDecimal("1000.00"));
		producto.setStock(10);
		producto = productoService.guardar(producto);

		Empleado empleado = new Empleado();
		empleado.setNombre("Empleado de prueba");
		empleado.setCargo("Cajero");
		empleado = empleadoService.guardar(empleado);

		VentaForm form = new VentaForm();
		form.setIdCliente(cliente.getIdCliente());
		form.setIdEmpleado(empleado.getIdEmpleado());
		LineaVentaForm linea = form.getLineas().get(0);
		linea.setIdProducto(producto.getIdProducto());
		linea.setCantidad(1);
		ventaService.registrar(form);
	}

}
