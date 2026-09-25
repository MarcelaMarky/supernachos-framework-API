package com.supernachos.servicio;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.supernachos.modelo.Usuario;
import com.supernachos.repositorio.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
@Transactional(readOnly = true)
public class AutenticacionService {

	/** Nombre del atributo de sesion donde se guarda el usuario que inicio sesion. */
	public static final String USUARIO_SESION = "usuarioSesion";

	private final UsuarioRepository usuarioRepository;

	public AutenticacionService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	/** Devuelve el usuario si el nombre y la clave coinciden con un registro de la tabla usuario. */
	public Optional<Usuario> autenticar(String usuario, String clave) {
		if (!StringUtils.hasText(usuario) || !StringUtils.hasText(clave)) {
			return Optional.empty();
		}
		return usuarioRepository.findByUsuario(usuario.trim()).filter(u -> u.getClave().equals(clave));
	}

	/**
	 * Descarta la sesion anterior y abre una nueva con el usuario autenticado, para
	 * que el identificador de sesion cambie despues de iniciar sesion.
	 */
	public void iniciarSesion(HttpServletRequest request, Usuario usuario) {
		HttpSession anterior = request.getSession(false);
		if (anterior != null) {
			anterior.invalidate();
		}
		request.getSession(true).setAttribute(USUARIO_SESION, usuario.getUsuario());
	}

	public void cerrarSesion(HttpServletRequest request) {
		HttpSession sesion = request.getSession(false);
		if (sesion != null) {
			sesion.invalidate();
		}
	}

	/** Usuario que tiene la sesion abierta, o null si nadie ha iniciado sesion. */
	public static String usuarioActual(HttpServletRequest request) {
		HttpSession sesion = request.getSession(false);
		return sesion == null ? null : (String) sesion.getAttribute(USUARIO_SESION);
	}

}
