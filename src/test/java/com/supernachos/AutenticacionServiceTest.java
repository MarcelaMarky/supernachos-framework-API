package com.supernachos;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supernachos.servicio.AutenticacionService;

/**
 * Pruebas del servicio de autenticacion. Usan el usuario admin / 1234 que crea
 * UsuarioInicial al arrancar la aplicacion.
 */
@SpringBootTest
class AutenticacionServiceTest {

	@Autowired
	private AutenticacionService autenticacionService;

	@Test
	void autenticaConUsuarioYClaveCorrectos() {
		assertThat(autenticacionService.autenticar("admin", "1234"))
				.hasValueSatisfying(usuario -> assertThat(usuario.getNombre()).isEqualTo("Administrador"));
	}

	@Test
	void ignoraLosEspaciosAlrededorDelUsuario() {
		assertThat(autenticacionService.autenticar("  admin  ", "1234")).isPresent();
	}

	@Test
	void rechazaUnaClaveIncorrecta() {
		assertThat(autenticacionService.autenticar("admin", "clave-incorrecta")).isEmpty();
	}

	@Test
	void rechazaUnUsuarioQueNoExiste() {
		assertThat(autenticacionService.autenticar("noexiste", "1234")).isEmpty();
	}

	@Test
	void rechazaCamposVacios() {
		assertThat(autenticacionService.autenticar("", "")).isEmpty();
		assertThat(autenticacionService.autenticar(null, "1234")).isEmpty();
		assertThat(autenticacionService.autenticar("admin", null)).isEmpty();
	}

}
