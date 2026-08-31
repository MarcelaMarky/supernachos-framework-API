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
import com.supernachos.modelo.Producto;
import com.supernachos.servicio.ProductoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	public String listar(@RequestParam(required = false) String busqueda, Model model) {
		model.addAttribute("productos", productoService.listar(busqueda));
		model.addAttribute("busqueda", busqueda);
		return "productos/listar";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("producto", new Producto());
		return "productos/formulario";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		model.addAttribute("producto", productoService.buscarPorId(id));
		return "productos/formulario";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute Producto producto, BindingResult errores, RedirectAttributes flash) {
		if (errores.hasErrors()) {
			return "productos/formulario";
		}
		boolean esNuevo = producto.getIdProducto() == null;
		productoService.guardar(producto);
		flash.addFlashAttribute("exito",
				esNuevo ? "Producto registrado correctamente." : "Producto actualizado correctamente.");
		return "redirect:/productos";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
		try {
			productoService.eliminar(id);
			flash.addFlashAttribute("exito", "Producto eliminado correctamente.");
		}
		catch (OperacionInvalidaException ex) {
			flash.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/productos";
	}

}
