package com.supernachos;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.supernachos.servicio.AutenticacionService;

/** Pruebas del inicio de sesion web, del interceptor de sesion y de los formularios. */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadWebTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void sinSesionLasPaginasRedirigenAlLogin() throws Exception {
		for (String ruta : new String[] { "/", "/productos", "/clientes", "/empleados", "/ventas" }) {
			mockMvc.perform(get(ruta))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/login"));
		}
	}

	@Test
	void loginWebConCredencialesValidasRedirigeAlInicio() throws Exception {
		mockMvc.perform(post("/login").param("usuario", "admin").param("clave", "1234"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	void loginWebConCredencialesInvalidasMuestraElError() throws Exception {
		mockMvc.perform(post("/login").param("usuario", "admin").param("clave", "mala"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andExpect(model().attribute("error", "Usuario o clave incorrectos."));
	}

	@Test
	void conSesionSeMuestranLasPaginasDeCadaModulo() throws Exception {
		String[][] paginas = { { "/", "inicio" }, { "/productos", "productos/listar" },
				{ "/clientes", "clientes/listar" }, { "/empleados", "empleados/listar" },
				{ "/ventas", "ventas/listar" }, { "/ventas/nueva", "ventas/formulario" } };
		for (String[] pagina : paginas) {
			mockMvc.perform(get(pagina[0]).sessionAttr(AutenticacionService.USUARIO_SESION, "admin"))
					.andExpect(status().isOk())
					.andExpect(view().name(pagina[1]));
		}
	}

	@Test
	void elFormularioDeVentaTraeLaFechaDeHoyEnFormatoDelNavegador() throws Exception {
		String hoy = LocalDate.now().toString(); // yyyy-MM-dd
		mockMvc.perform(get("/ventas/nueva").sessionAttr(AutenticacionService.USUARIO_SESION, "admin"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("name=\"fecha\" value=\"" + hoy + "\"")));
	}

	@Test
	void guardarProductoInvalidoVuelveAlFormularioConErrores() throws Exception {
		mockMvc.perform(post("/productos/guardar").sessionAttr(AutenticacionService.USUARIO_SESION, "admin")
				.param("nombre", "").param("categoria", "Snacks").param("precio", "-10").param("stock", "5"))
				.andExpect(status().isOk())
				.andExpect(view().name("productos/formulario"))
				.andExpect(model().attributeHasFieldErrors("producto", "nombre", "precio"));
	}

	@Test
	void consultarUnRegistroInexistenteMuestraLaPaginaDeError() throws Exception {
		mockMvc.perform(get("/productos/editar/999999").sessionAttr(AutenticacionService.USUARIO_SESION, "admin"))
				.andExpect(status().isOk())
				.andExpect(view().name("error-app"))
				.andExpect(model().attribute("titulo", "Registro no encontrado"));
	}

}
