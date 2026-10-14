package com.supernachos.formulario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

/**
 * Datos que llegan desde el formulario de ventas. Se usa un formulario aparte de
 * la entidad porque el precio y el total no los escribe el usuario: los calcula
 * el servicio a partir del producto seleccionado.
 */
public class VentaForm {

	private Integer idVenta;

	/** Formato yyyy-MM-dd, el unico que acepta el campo input type="date" del navegador. */
	@NotNull(message = "La fecha es obligatoria")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fecha = LocalDate.now();

	@NotNull(message = "Debe seleccionar un cliente")
	private Integer idCliente;

	@NotNull(message = "Debe seleccionar un empleado")
	private Integer idEmpleado;

	private List<LineaVentaForm> lineas = new ArrayList<>();

	public VentaForm() {
		lineas.add(new LineaVentaForm());
	}

	public Integer getIdVenta() {
		return idVenta;
	}

	public void setIdVenta(Integer idVenta) {
		this.idVenta = idVenta;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public Integer getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(Integer idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public List<LineaVentaForm> getLineas() {
		return lineas;
	}

	public void setLineas(List<LineaVentaForm> lineas) {
		this.lineas = lineas;
	}

}
