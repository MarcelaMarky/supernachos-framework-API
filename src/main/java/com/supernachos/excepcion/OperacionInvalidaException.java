package com.supernachos.excepcion;

/** Se lanza cuando una regla del negocio impide completar la operacion. */
public class OperacionInvalidaException extends RuntimeException {

	public OperacionInvalidaException(String mensaje) {
		super(mensaje);
	}

}
