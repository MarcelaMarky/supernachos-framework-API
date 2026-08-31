package com.supernachos.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supernachos.modelo.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

	List<Producto> findByNombreContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrderByNombreAsc(String nombre, String categoria);

	List<Producto> findAllByOrderByNombreAsc();

}
