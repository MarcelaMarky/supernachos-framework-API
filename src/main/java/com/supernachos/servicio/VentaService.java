package com.supernachos.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supernachos.excepcion.OperacionInvalidaException;
import com.supernachos.excepcion.RecursoNoEncontradoException;
import com.supernachos.formulario.LineaVentaForm;
import com.supernachos.formulario.VentaForm;
import com.supernachos.modelo.DetalleVenta;
import com.supernachos.modelo.Producto;
import com.supernachos.modelo.Venta;
import com.supernachos.repositorio.VentaRepository;

@Service
@Transactional
public class VentaService {

	private final VentaRepository ventaRepository;

	private final ProductoService productoService;

	private final ClienteService clienteService;

	private final EmpleadoService empleadoService;

	public VentaService(VentaRepository ventaRepository, ProductoService productoService, ClienteService clienteService,
			EmpleadoService empleadoService) {
		this.ventaRepository = ventaRepository;
		this.productoService = productoService;
		this.clienteService = clienteService;
		this.empleadoService = empleadoService;
	}

	@Transactional(readOnly = true)
	public List<Venta> listar() {
		return ventaRepository.findAllByOrderByIdVentaDesc();
	}

	@Transactional(readOnly = true)
	public Venta buscarPorId(Integer id) {
		return ventaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe la venta con id " + id));
	}

	/**
	 * Registra la venta: calcula subtotales y total con el precio actual de cada
	 * producto y descuenta el stock del inventario.
	 */
	public Venta registrar(VentaForm form) {
		List<LineaVentaForm> lineas = lineasDiligenciadas(form);
		if (lineas.isEmpty()) {
			throw new OperacionInvalidaException("La venta debe tener al menos un producto.");
		}

		Venta venta = new Venta();
		venta.setFecha(form.getFecha() != null ? form.getFecha() : LocalDate.now());
		venta.setCliente(clienteService.buscarPorId(form.getIdCliente()));
		venta.setEmpleado(empleadoService.buscarPorId(form.getIdEmpleado()));

		BigDecimal total = BigDecimal.ZERO;
		for (LineaVentaForm linea : lineas) {
			Producto producto = productoService.buscarPorId(linea.getIdProducto());
			int cantidad = linea.getCantidad() == null ? 0 : linea.getCantidad();
			if (cantidad <= 0) {
				throw new OperacionInvalidaException(
						"La cantidad de '" + producto.getNombre() + "' debe ser mayor que cero.");
			}
			if (producto.getStock() == null || producto.getStock() < cantidad) {
				throw new OperacionInvalidaException("Stock insuficiente de '" + producto.getNombre() + "'. Disponible: "
						+ (producto.getStock() == null ? 0 : producto.getStock()) + ".");
			}

			BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
			DetalleVenta detalle = new DetalleVenta();
			detalle.setProducto(producto);
			detalle.setCantidad(cantidad);
			detalle.setSubtotal(subtotal);
			venta.agregarDetalle(detalle);
			total = total.add(subtotal);

			producto.setStock(producto.getStock() - cantidad);
			productoService.guardar(producto);
		}

		venta.setTotal(total);
		return ventaRepository.save(venta);
	}

	/** Anula la venta y devuelve al inventario las unidades que se habian descontado. */
	public void eliminar(Integer id) {
		Venta venta = buscarPorId(id);
		for (DetalleVenta detalle : venta.getDetalles()) {
			Producto producto = detalle.getProducto();
			if (producto != null && detalle.getCantidad() != null) {
				int stockActual = producto.getStock() == null ? 0 : producto.getStock();
				producto.setStock(stockActual + detalle.getCantidad());
				productoService.guardar(producto);
			}
		}
		ventaRepository.delete(venta);
	}

	@Transactional(readOnly = true)
	public long contar() {
		return ventaRepository.count();
	}

	@Transactional(readOnly = true)
	public BigDecimal totalVendido() {
		return ventaRepository.findAll().stream().map(Venta::getTotal).filter(java.util.Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private List<LineaVentaForm> lineasDiligenciadas(VentaForm form) {
		List<LineaVentaForm> lineas = new ArrayList<>();
		if (form.getLineas() == null) {
			return lineas;
		}
		for (LineaVentaForm linea : form.getLineas()) {
			if (linea != null && !linea.estaVacia()) {
				lineas.add(linea);
			}
		}
		return lineas;
	}

}
