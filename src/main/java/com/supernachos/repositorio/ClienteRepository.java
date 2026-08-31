package com.supernachos.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supernachos.modelo.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

	List<Cliente> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

	List<Cliente> findAllByOrderByNombreAsc();

}
