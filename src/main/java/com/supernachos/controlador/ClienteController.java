package com.supernachos.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.supernachos.excepcion.OperacionInvalidaException;
import com.supernachos.modelo.Cliente;
import com.supernachos.servicio.ClienteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@GetMapping
	public String listar(@RequestParam(required = false) String busqueda, Model model) {
		model.addAttribute("clientes", clienteService.listar(busqueda));
		model.addAttribute("busqueda", busqueda);
		return "clientes/listar";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("cliente", new Cliente());
		return "clientes/formulario";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		model.addAttribute("cliente", clienteService.buscarPorId(id));
		return "clientes/formulario";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute Cliente cliente, BindingResult errores, RedirectAttributes flash) {
		if (errores.hasErrors()) {
			return "clientes/formulario";
		}
		boolean esNuevo = cliente.getIdCliente() == null;
		clienteService.guardar(cliente);
		flash.addFlashAttribute("exito",
				esNuevo ? "Cliente registrado correctamente." : "Cliente actualizado correctamente.");
		return "redirect:/clientes";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
		try {
			clienteService.eliminar(id);
			flash.addFlashAttribute("exito", "Cliente eliminado correctamente.");
		}
		catch (OperacionInvalidaException ex) {
			flash.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/clientes";
	}

}
