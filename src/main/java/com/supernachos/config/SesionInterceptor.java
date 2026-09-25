package com.supernachos.config;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.supernachos.servicio.AutenticacionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Bloquea el acceso a quien no ha iniciado sesion: las paginas se redirigen al
 * login y las peticiones a la API reciben un 401 en JSON.
 */
@Component
public class SesionInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (AutenticacionService.usuarioActual(request) != null) {
			return true;
		}
		String ruta = request.getRequestURI().substring(request.getContextPath().length());
		if (ruta.startsWith("/api/")) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.getWriter().write("{\"error\":\"Debe iniciar sesion en /api/login\"}");
		}
		else {
			response.sendRedirect(request.getContextPath() + "/login");
		}
		return false;
	}

}
