package com.supernachos.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.supernachos.excepcion.OperacionInvalidaException;
import com.supernachos.excepcion.RecursoNoEncontradoException;
import com.supernachos.modelo.Cliente;
import com.supernachos.repositorio.ClienteRepository;
import com.supernachos.repositorio.VentaRepository;

@Service
@Transactional
public class ClienteService {

	private final ClienteRepository clienteRepository;

	private final VentaRepository ventaRepository;

	public ClienteService(ClienteRepository clienteRepository, VentaRepository ventaRepository) {
		this.clienteRepository = clienteRepository;
		this.ventaRepository = ventaRepository;
	}

	@Transactional(readOnly = true)
	public List<Cliente> listar(String busqueda) {
		if (!StringUtils.hasText(busqueda)) {
			return clienteRepository.findAllByOrderByNombreAsc();
		}
		return clienteRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(busqueda);
	}

	@Transactional(readOnly = true)
	public Cliente buscarPorId(Integer id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
	}

	public Cliente guardar(Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	public void eliminar(Integer id) {
		Cliente cliente = buscarPorId(id);
		if (ventaRepository.existsByClienteIdCliente(id)) {
			throw new OperacionInvalidaException(
					"No se puede eliminar a '" + cliente.getNombre() + "' porque tiene ventas registradas.");
		}
		clienteRepository.delete(cliente);
	}

	@Transactional(readOnly = true)
	public long contar() {
		return clienteRepository.count();
	}

}
