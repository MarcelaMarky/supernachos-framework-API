package com.supernachos.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.supernachos.excepcion.OperacionInvalidaException;
import com.supernachos.excepcion.RecursoNoEncontradoException;
import com.supernachos.modelo.Producto;
import com.supernachos.repositorio.DetalleVentaRepository;
import com.supernachos.repositorio.ProductoRepository;

@Service
@Transactional
public class ProductoService {

	private final ProductoRepository productoRepository;

	private final DetalleVentaRepository detalleVentaRepository;

	public ProductoService(ProductoRepository productoRepository, DetalleVentaRepository detalleVentaRepository) {
		this.productoRepository = productoRepository;
		this.detalleVentaRepository = detalleVentaRepository;
	}

	@Transactional(readOnly = true)
	public List<Producto> listar(String busqueda) {
		if (!StringUtils.hasText(busqueda)) {
			return productoRepository.findAllByOrderByNombreAsc();
		}
		return productoRepository
				.findByNombreContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrderByNombreAsc(busqueda, busqueda);
	}

	@Transactional(readOnly = true)
	public Producto buscarPorId(Integer id) {
		return productoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
	}

	public Producto guardar(Producto producto) {
		return productoRepository.save(producto);
	}

	public void eliminar(Integer id) {
		Producto producto = buscarPorId(id);
		if (detalleVentaRepository.existsByProductoIdProducto(id)) {
			throw new OperacionInvalidaException(
					"No se puede eliminar '" + producto.getNombre() + "' porque tiene ventas registradas.");
		}
		productoRepository.delete(producto);
	}

	@Transactional(readOnly = true)
	public long contar() {
		return productoRepository.count();
	}

}
