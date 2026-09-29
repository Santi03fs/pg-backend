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
			if (usuarioRepository.findByUsername("admin").isEmpty()) {
				String hashedPass = org.springframework.security.crypto.bcrypt.BCrypt.hashpw("pg2026", org.springframework.security.crypto.bcrypt.BCrypt.gensalt(12));
				Usuario defaultAdmin = new Usuario("admin", hashedPass, "ADMIN", "Administrador Principal");
				usuarioRepository.save(defaultAdmin);
				System.out.println("====== BOOTSTRAP: Creado usuario administrador por defecto (admin / pg2026) con hash BCrypt ======");
			}

			if (usuarioRepository.findByUsername("jefe").isEmpty()) {
				String hashedPass = org.springframework.security.crypto.bcrypt.BCrypt.hashpw("jefe2026", org.springframework.security.crypto.bcrypt.BCrypt.gensalt(12));
				Usuario defaultJefe = new Usuario("jefe", hashedPass, "JEFE", "Jefe de Obra");
				usuarioRepository.save(defaultJefe);
				System.out.println("====== BOOTSTRAP: Creado usuario jefe por defecto (jefe / jefe2026) con hash BCrypt ======");
			}

			if (usuarioRepository.findByUsername("oficina").isEmpty()) {
				String hashedPass = org.springframework.security.crypto.bcrypt.BCrypt.hashpw("oficina2026", org.springframework.security.crypto.bcrypt.BCrypt.gensalt(12));
				Usuario defaultOficina = new Usuario("oficina", hashedPass, "OFICINA", "Personal de Oficina");
				usuarioRepository.save(defaultOficina);
				System.out.println("====== BOOTSTRAP: Creado usuario oficina por defecto (oficina / oficina2026) con hash BCrypt ======");
			}
		};
	}

}

