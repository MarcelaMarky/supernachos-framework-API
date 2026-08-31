package com.supernachos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supernachos.excepcion.OperacionInvalidaException;
import com.supernachos.formulario.LineaVentaForm;
import com.supernachos.formulario.VentaForm;
import com.supernachos.modelo.Cliente;
import com.supernachos.modelo.Empleado;
import com.supernachos.modelo.Producto;
import com.supernachos.modelo.Venta;
import com.supernachos.servicio.ClienteService;
import com.supernachos.servicio.EmpleadoService;
import com.supernachos.servicio.ProductoService;
import com.supernachos.servicio.VentaService;

@SpringBootTest
class VentaServiceTest {

	@Autowired
	private VentaService ventaService;

	@Autowired
	private ProductoService productoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private EmpleadoService empleadoService;

	private Producto producto;

	private Cliente cliente;

	private Empleado empleado;

	@BeforeEach
	void prepararDatos() {
		producto = new Producto();
		producto.setNombre("Nachos con queso");
		producto.setCategoria("Snacks");
		producto.setPrecio(new BigDecimal("3500.00"));
		producto.setStock(10);
		producto = productoService.guardar(producto);

		cliente = new Cliente();
		cliente.setNombre("Ana Ruiz");
		cliente = clienteService.guardar(cliente);

		empleado = new Empleado();
		empleado.setNombre("Carlos Perez");
		empleado.setCargo("Cajero");
		empleado = empleadoService.guardar(empleado);
	}

	@Test
	void registrarVentaCalculaElTotalYDescuentaElStock() {
		Venta venta = ventaService.registrar(formularioCon(3));

		assertThat(venta.getTotal()).isEqualByComparingTo("10500.00");
		assertThat(venta.getDetalles()).hasSize(1);
		assertThat(productoService.buscarPorId(producto.getIdProducto()).getStock()).isEqualTo(7);
	}

	@Test
	void noPermiteVenderMasUnidadesDeLasDisponibles() {
		assertThatThrownBy(() -> ventaService.registrar(formularioCon(50)))
				.isInstanceOf(OperacionInvalidaException.class)
				.hasMessageContaining("Stock insuficiente");
	}

	@Test
	void anularVentaDevuelveElStockAlInventario() {
		Venta venta = ventaService.registrar(formularioCon(4));
		assertThat(productoService.buscarPorId(producto.getIdProducto()).getStock()).isEqualTo(6);

		ventaService.eliminar(venta.getIdVenta());

		assertThat(productoService.buscarPorId(producto.getIdProducto()).getStock()).isEqualTo(10);
	}

	private VentaForm formularioCon(int cantidad) {
		VentaForm form = new VentaForm();
		form.setIdCliente(cliente.getIdCliente());
		form.setIdEmpleado(empleado.getIdEmpleado());
		LineaVentaForm linea = form.getLineas().get(0);
		linea.setIdProducto(producto.getIdProducto());
		linea.setCantidad(cantidad);
		return form;
	}

}
