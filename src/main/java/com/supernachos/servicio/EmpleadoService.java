package com.supernachos.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.supernachos.excepcion.OperacionInvalidaException;
import com.supernachos.excepcion.RecursoNoEncontradoException;
import com.supernachos.modelo.Empleado;
import com.supernachos.repositorio.EmpleadoRepository;
import com.supernachos.repositorio.VentaRepository;

@Service
@Transactional
public class EmpleadoService {

	private final EmpleadoRepository empleadoRepository;

	private final VentaRepository ventaRepository;

	public EmpleadoService(EmpleadoRepository empleadoRepository, VentaRepository ventaRepository) {
		this.empleadoRepository = empleadoRepository;
		this.ventaRepository = ventaRepository;
	}

	@Transactional(readOnly = true)
	public List<Empleado> listar(String busqueda) {
		if (!StringUtils.hasText(busqueda)) {
			return empleadoRepository.findAllByOrderByNombreAsc();
		}
		return empleadoRepository.findByNombreContainingIgnoreCaseOrCargoContainingIgnoreCaseOrderByNombreAsc(busqueda,
				busqueda);
	}

	@Transactional(readOnly = true)
	public Empleado buscarPorId(Integer id) {
		return empleadoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe el empleado con id " + id));
	}

	public Empleado guardar(Empleado empleado) {
		return empleadoRepository.save(empleado);
	}

	public void eliminar(Integer id) {
		Empleado empleado = buscarPorId(id);
		if (ventaRepository.existsByEmpleadoIdEmpleado(id)) {
			throw new OperacionInvalidaException(
					"No se puede eliminar a '" + empleado.getNombre() + "' porque tiene ventas registradas.");
		}
		empleadoRepository.delete(empleado);
	}

	@Transactional(readOnly = true)
	public long contar() {
		return empleadoRepository.count();
	}

}
