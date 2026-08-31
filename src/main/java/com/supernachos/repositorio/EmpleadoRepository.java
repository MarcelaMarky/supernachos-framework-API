package com.supernachos.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supernachos.modelo.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

	List<Empleado> findByNombreContainingIgnoreCaseOrCargoContainingIgnoreCaseOrderByNombreAsc(String nombre, String cargo);

	List<Empleado> findAllByOrderByNombreAsc();

}
