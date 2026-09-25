package com.supernachos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.supernachos.modelo.Usuario;
import com.supernachos.repositorio.UsuarioRepository;

/** Crea el usuario administrador la primera vez que arranca la aplicacion. */
@Component
public class UsuarioInicial implements CommandLineRunner {

	private final UsuarioRepository usuarioRepository;

	public UsuarioInicial(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public void run(String... args) {
		if (usuarioRepository.count() == 0) {
			Usuario admin = new Usuario();
			admin.setUsuario("admin");
			admin.setClave("1234");
			admin.setNombre("Administrador");
			usuarioRepository.save(admin);
		}
	}

}
