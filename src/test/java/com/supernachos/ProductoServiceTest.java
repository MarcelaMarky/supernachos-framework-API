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

/** Pruebas del modulo de productos. Cada prueba se revierte al terminar. */
@SpringBootTest
@Transactional
class ProductoServiceTest {

	@Autowired
	private ProductoService productoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private EmpleadoService empleadoService;

	@Autowired
	private VentaService ventaService;

	@Autowired
	private Validator validator;

	@Test
	void guardarAsignaIdYPermiteConsultarElProducto() {
		Producto guardado = productoService.guardar(producto("Gaseosa 400 ml", "Bebidas", "3200.00", 60));

		assertThat(guardado.getIdProducto()).isNotNull();
		Producto consultado = productoService.buscarPorId(guardado.getIdProducto());
		assertThat(consultado.getNombre()).isEqualTo("Gaseosa 400 ml");
		assertThat(consultado.getPrecio()).isEqualByComparingTo("3200.00");
		assertThat(consultado.getStock()).isEqualTo(60);
	}

	@Test
	void editarActualizaLosDatosDelProducto() {
		Producto guardado = productoService.guardar(producto("Salsa picante", "Salsas", "4800.00", 25));

		guardado.setPrecio(new BigDecimal("5000.00"));
		guardado.setStock(30);
		productoService.guardar(guardado);

		Producto consultado = productoService.buscarPorId(guardado.getIdProducto());
		assertThat(consultado.getPrecio()).isEqualByComparingTo("5000.00");
		assertThat(consultado.getStock()).isEqualTo(30);
	}

	@Test
	void buscarFiltraPorNombreOCategoriaSinImportarMayusculas() {
		productoService.guardar(producto("Guacamole personal", "SalsasPrueba", "5200.00", 18));
		productoService.guardar(producto("Queso fundido", "SalsasPrueba", "4500.00", 10));
		productoService.guardar(producto("Agua 600 ml", "BebidasPrueba", "2000.00", 40));

		List<String> porCategoria = productoService.listar("salsasprueba").stream().map(Producto::getNombre).toList();
		List<String> porNombre = productoService.listar("GUACAMOLE").stream().map(Producto::getNombre).toList();

		assertThat(porCategoria).containsExactly("Guacamole personal", "Queso fundido");
		assertThat(porNombre).containsExactly("Guacamole personal");
	}

	@Test
	void buscarUnIdQueNoExisteLanzaExcepcion() {
		assertThatThrownBy(() -> productoService.buscarPorId(999999))
				.isInstanceOf(RecursoNoEncontradoException.class)
				.hasMessageContaining("No existe el producto");
	}

	@Test
	void eliminarProductoSinVentasLoBorra() {
		Producto guardado = productoService.guardar(producto("Producto temporal", "Varios", "1000.00", 5));

		productoService.eliminar(guardado.getIdProducto());

		assertThatThrownBy(() -> productoService.buscarPorId(guardado.getIdProducto()))
				.isInstanceOf(RecursoNoEncontradoException.class);
	}

	@Test
	void noPermiteEliminarUnProductoConVentas() {
		Producto vendido = productoService.guardar(producto("Nachos con queso", "Snacks", "6500.00", 40));
		registrarVentaDe(vendido, 2);

		assertThatThrownBy(() -> productoService.eliminar(vendido.getIdProducto()))
				.isInstanceOf(OperacionInvalidaException.class)
				.hasMessageContaining("tiene ventas registradas");
	}

	@Test
	void validaCamposObligatoriosYValoresNoNegativos() {
		Producto invalido = producto("", "Snacks", "-1.00", -5);

		List<String> camposConError = validator.validate(invalido).stream()
				.map(ConstraintViolation::getPropertyPath).map(Object::toString).toList();

		assertThat(camposConError).containsExactlyInAnyOrder("nombre", "precio", "stock");
	}

	private Producto producto(String nombre, String categoria, String precio, int stock) {
		Producto producto = new Producto();
		producto.setNombre(nombre);
		producto.setCategoria(categoria);
		producto.setPrecio(new BigDecimal(precio));
		producto.setStock(stock);
		return producto;
	}

	private void registrarVentaDe(Producto producto, int cantidad) {
		Cliente cliente = new Cliente();
		cliente.setNombre("Cliente de prueba");
		cliente = clienteService.guardar(cliente);

		Empleado empleado = new Empleado();
		empleado.setNombre("Empleado de prueba");
		empleado.setCargo("Cajero");
		empleado = empleadoService.guardar(empleado);

		VentaForm form = new VentaForm();
		form.setIdCliente(cliente.getIdCliente());
		form.setIdEmpleado(empleado.getIdEmpleado());
		LineaVentaForm linea = form.getLineas().get(0);
		linea.setIdProducto(producto.getIdProducto());
		linea.setCantidad(cantidad);
		ventaService.registrar(form);
	}

}
