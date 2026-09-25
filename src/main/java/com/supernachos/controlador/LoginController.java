package com.supernachos.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.supernachos.modelo.Usuario;
import com.supernachos.servicio.AutenticacionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

	private final AutenticacionService autenticacionService;

	public LoginController(AutenticacionService autenticacionService) {
		this.autenticacionService = autenticacionService;
	}

	@GetMapping("/login")
	public String login(HttpServletRequest request) {
		if (AutenticacionService.usuarioActual(request) != null) {
			return "redirect:/";
		}
		return "login";
	}

	@PostMapping("/login")
	public String ingresar(@RequestParam String usuario, @RequestParam String clave, HttpServletRequest request,
			Model model) {
		Usuario encontrado = autenticacionService.autenticar(usuario, clave).orElse(null);
		if (encontrado == null) {
			model.addAttribute("error", "Usuario o clave incorrectos.");
			model.addAttribute("usuario", usuario);
			return "login";
		}
		autenticacionService.iniciarSesion(request, encontrado);
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String salir(HttpServletRequest request, RedirectAttributes flash) {
		autenticacionService.cerrarSesion(request);
		flash.addFlashAttribute("exito", "Sesion cerrada correctamente.");
		return "redirect:/login";
	}

}
