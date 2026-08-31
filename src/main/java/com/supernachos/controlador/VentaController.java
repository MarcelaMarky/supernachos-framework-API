package com.supernachos.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.supernachos.excepcion.OperacionInvalidaException;
import com.supernachos.formulario.VentaForm;
import com.supernachos.servicio.ClienteService;
import com.supernachos.servicio.EmpleadoService;
import com.supernachos.servicio.ProductoService;
import com.supernachos.servicio.VentaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/ventas")
public class VentaController {

	private final VentaService ventaService;

	private final ProductoService productoService;

	private final ClienteService clienteService;

	private final EmpleadoService empleadoService;

	public VentaController(VentaService ventaService, ProductoService productoService, ClienteService clienteService,
			EmpleadoService empleadoService) {
		this.ventaService = ventaService;
		this.productoService = productoService;
		this.clienteService = clienteService;
		this.empleadoService = empleadoService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("ventas", ventaService.listar());
		return "ventas/listar";
	}

	@GetMapping("/nueva")
	public String nueva(Model model) {
		model.addAttribute("ventaForm", new VentaForm());
		cargarCombos(model);
		return "ventas/formulario";
	}

	@GetMapping("/{id}")
	public String detalle(@PathVariable Integer id, Model model) {
		model.addAttribute("venta", ventaService.buscarPorId(id));
		return "ventas/detalle";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("ventaForm") VentaForm ventaForm, BindingResult errores, Model model,
			RedirectAttributes flash) {
		if (errores.hasErrors()) {
			cargarCombos(model);
			return "ventas/formulario";
		}
		try {
			ventaService.registrar(ventaForm);
		}
		catch (OperacionInvalidaException ex) {
			model.addAttribute("error", ex.getMessage());
			cargarCombos(model);
			return "ventas/formulario";
		}
		flash.addFlashAttribute("exito", "Venta registrada correctamente. El stock fue actualizado.");
		return "redirect:/ventas";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
		ventaService.eliminar(id);
		flash.addFlashAttribute("exito", "Venta anulada. Las unidades regresaron al inventario.");
		return "redirect:/ventas";
	}

	private void cargarCombos(Model model) {
		model.addAttribute("productos", productoService.listar(null));
		model.addAttribute("clientes", clienteService.listar(null));
		model.addAttribute("empleados", empleadoService.listar(null));
	}

}
