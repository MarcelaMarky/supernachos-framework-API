package com.supernachos.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supernachos.servicio.AutenticacionService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class AuthApiController {

	private final AutenticacionService autenticacionService;

	public AuthApiController(AutenticacionService autenticacionService) {
		this.autenticacionService = autenticacionService;
	}

	/** Datos que llegan en el cuerpo JSON: {"usuario": "...", "clave": "..."} */
	public record Credenciales(String usuario, String clave) {
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody Credenciales credenciales,
			HttpServletRequest request) {
		return autenticacionService.autenticar(credenciales.usuario(), credenciales.clave())
				.map(usuario -> {
					autenticacionService.iniciarSesion(request, usuario);
					return ResponseEntity.ok(Map.of("mensaje", "Inicio de sesion correcto", "usuario",
							usuario.getUsuario()));
				})
				.orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("error", "Usuario o clave incorrectos")));
	}

	@GetMapping("/sesion")
	public Map<String, String> sesion(HttpServletRequest request) {
		return Map.of("usuario", AutenticacionService.usuarioActual(request));
	}

	@PostMapping("/logout")
	public Map<String, String> logout(HttpServletRequest request) {
		autenticacionService.cerrarSesion(request);
		return Map.of("mensaje", "Sesion cerrada");
	}

}
