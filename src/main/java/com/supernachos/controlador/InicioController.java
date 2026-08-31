package com.supernachos.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.supernachos.servicio.ClienteService;
import com.supernachos.servicio.EmpleadoService;
import com.supernachos.servicio.ProductoService;
import com.supernachos.servicio.VentaService;

@Controller
public class InicioController {

	private final ProductoService productoService;

	private final ClienteService clienteService;

	private final EmpleadoService empleadoService;

	private final VentaService ventaService;

	public InicioController(ProductoService productoService, ClienteService clienteService,
			EmpleadoService empleadoService, VentaService ventaService) {
		this.productoService = productoService;
		this.clienteService = clienteService;
		this.empleadoService = empleadoService;
		this.ventaService = ventaService;
	}

	@GetMapping("/")
	public String inicio(Model model) {
		model.addAttribute("totalProductos", productoService.contar());
		model.addAttribute("totalClientes", clienteService.contar());
		model.addAttribute("totalEmpleados", empleadoService.contar());
		model.addAttribute("totalVentas", ventaService.contar());
		model.addAttribute("montoVendido", ventaService.totalVendido());
		model.addAttribute("ultimasVentas", ventaService.listar().stream().limit(5).toList());
		return "inicio";
	}

}
