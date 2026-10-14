package com.supernachos;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Levanta el servidor Tomcat real para comprobar que, al iniciar sesion desde
 * un navegador nuevo, la redireccion no lleva ;jsessionid en la URL (esa
 * direccion respondia 404).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SesionCookieTest {

	@LocalServerPort
	private int puerto;

	@Test
	void despuesDelLoginSeRedirigeAlInicioSinJsessionidEnLaUrl() throws Exception {
		HttpClient cliente = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();
		HttpRequest login = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + "/login"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString("usuario=admin&clave=1234"))
				.build();

		HttpResponse<Void> respuesta = cliente.send(login, HttpResponse.BodyHandlers.discarding());

		assertThat(respuesta.statusCode()).isEqualTo(302);
		assertThat(respuesta.headers().firstValue("Location")).hasValue("http://localhost:" + puerto + "/");
		assertThat(respuesta.headers().firstValue("Set-Cookie")).hasValueSatisfying(
				cookie -> assertThat(cookie).startsWith("JSESSIONID="));
	}

}
