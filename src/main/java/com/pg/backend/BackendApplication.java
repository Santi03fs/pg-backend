package com.pg.backend;

import com.pg.backend.model.Usuario;
import com.pg.backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner bootstrapData(UsuarioRepository usuarioRepository) {
		return args -> {
			if (usuarioRepository.count() == 0) {
				Usuario defaultAdmin = new Usuario("admin", "pg2026", "ADMIN", "Administrador Principal");
				usuarioRepository.save(defaultAdmin);
				System.out.println("====== BOOTSTRAP: Creado usuario administrador por defecto (admin / pg2026) ======");
			}
		};
	}

}

