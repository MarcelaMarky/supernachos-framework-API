package com.supernachos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

/** Pruebas de la API REST de autenticacion (/api/login, /api/sesion, /api/logout). */
@SpringBootTest
@AutoConfigureMockMvc
class AuthApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void consultarLaSesionSinIniciarSesionResponde401() throws Exception {
		mockMvc.perform(get("/api/sesion"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error").value("Debe iniciar sesion en /api/login"));
	}

	@Test
	void loginConClaveIncorrectaResponde401() throws Exception {
		mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"usuario\": \"admin\", \"clave\": \"claveIncorrecta\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error").value("Usuario o clave incorrectos"));
	}

	@Test
	void loginConCamposVaciosResponde401() throws Exception {
		mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"usuario\": \"\", \"clave\": \"\"}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void loginCorrectoResponde200YLaSesionQuedaActiva() throws Exception {
		MockHttpSession sesion = iniciarSesion();

		mockMvc.perform(get("/api/sesion").session(sesion))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.usuario").value("admin"));
	}

	@Test
	void logoutCierraLaSesion() throws Exception {
		MockHttpSession sesion = iniciarSesion();

		mockMvc.perform(post("/api/logout").session(sesion))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.mensaje").value("Sesion cerrada"));

		mockMvc.perform(get("/api/sesion").session(sesion))
				.andExpect(status().isUnauthorized());
	}

	private MockHttpSession iniciarSesion() throws Exception {
		return (MockHttpSession) mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"usuario\": \"admin\", \"clave\": \"1234\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.mensaje").value("Inicio de sesion correcto"))
				.andExpect(jsonPath("$.usuario").value("admin"))
				.andReturn().getRequest().getSession(false);
	}

}
