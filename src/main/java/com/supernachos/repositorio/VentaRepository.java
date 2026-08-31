package com.supernachos.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supernachos.modelo.Venta;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

	List<Venta> findAllByOrderByIdVentaDesc();

	boolean existsByClienteIdCliente(Integer idCliente);

	boolean existsByEmpleadoIdEmpleado(Integer idEmpleado);

}
