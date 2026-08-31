package com.supernachos.controlador;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.supernachos.excepcion.OperacionInvalidaException;
import com.supernachos.excepcion.RecursoNoEncontradoException;

@ControllerAdvice
public class ManejadorErrores {

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public String noEncontrado(RecursoNoEncontradoException ex, Model model) {
		model.addAttribute("titulo", "Registro no encontrado");
		model.addAttribute("mensaje", ex.getMessage());
		return "error-app";
	}

	@ExceptionHandler(OperacionInvalidaException.class)
	public String operacionInvalida(OperacionInvalidaException ex, Model model) {
		model.addAttribute("titulo", "No se pudo completar la operacion");
		model.addAttribute("mensaje", ex.getMessage());
		return "error-app";
	}

}
