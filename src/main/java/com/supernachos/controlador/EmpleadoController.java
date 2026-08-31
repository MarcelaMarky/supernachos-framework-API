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
import com.supernachos.modelo.Empleado;
import com.supernachos.servicio.EmpleadoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

	private final EmpleadoService empleadoService;

	public EmpleadoController(EmpleadoService empleadoService) {
		this.empleadoService = empleadoService;
	}

	@GetMapping
	public String listar(@RequestParam(required = false) String busqueda, Model model) {
		model.addAttribute("empleados", empleadoService.listar(busqueda));
		model.addAttribute("busqueda", busqueda);
		return "empleados/listar";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("empleado", new Empleado());
		return "empleados/formulario";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		model.addAttribute("empleado", empleadoService.buscarPorId(id));
		return "empleados/formulario";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute Empleado empleado, BindingResult errores, RedirectAttributes flash) {
		if (errores.hasErrors()) {
			return "empleados/formulario";
		}
		boolean esNuevo = empleado.getIdEmpleado() == null;
		empleadoService.guardar(empleado);
		flash.addFlashAttribute("exito",
				esNuevo ? "Empleado registrado correctamente." : "Empleado actualizado correctamente.");
		return "redirect:/empleados";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
		try {
			empleadoService.eliminar(id);
			flash.addFlashAttribute("exito", "Empleado eliminado correctamente.");
		}
		catch (OperacionInvalidaException ex) {
			flash.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/empleados";
	}

}
