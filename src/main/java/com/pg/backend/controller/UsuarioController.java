package com.pg.backend.controller;

import com.pg.backend.model.Usuario;
import com.pg.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los usuarios
    @GetMapping
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Crear o actualizar un usuario
    @PostMapping
    public ResponseEntity<?> guardarUsuario(@RequestBody Usuario usuario) {
        // Si es un usuario nuevo, verificar que el username no esté duplicado
        if (usuario.getId() == null) {
            Optional<Usuario> existente = usuarioRepository.findByUsername(usuario.getUsername().trim().toLowerCase());
            if (existente.isPresent()) {
                return ResponseEntity.badRequest().body("El nombre de usuario ya está registrado.");
            }
        }
        
        usuario.setUsername(usuario.getUsername().trim().toLowerCase());
        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(guardado);
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al intentar eliminar el usuario.");
        }
    }

    // Endpoint de login (autenticación)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        String cleanUser = loginRequest.getUsername().trim().toLowerCase();
        String cleanPass = loginRequest.getPassword().trim();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(cleanUser);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getPassword().equals(cleanPass)) {
                return ResponseEntity.ok(usuario); // Retorna los datos del usuario (rol, nombre, etc)
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
    }
}
