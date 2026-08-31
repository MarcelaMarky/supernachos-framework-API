package com.supernachos.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supernachos.modelo.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

	boolean existsByProductoIdProducto(Integer idProducto);

}
