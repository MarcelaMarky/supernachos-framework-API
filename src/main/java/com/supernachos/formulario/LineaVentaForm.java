package com.supernachos.formulario;

/** Una linea del formulario de venta: que producto y cuantas unidades. */
public class LineaVentaForm {

	private Integer idProducto;

	private Integer cantidad;

	public boolean estaVacia() {
		return idProducto == null;
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

}
