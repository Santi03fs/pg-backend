package com.pg.backend.controller;

import com.pg.backend.model.Usuario;
import com.pg.backend.repository.UsuarioRepository;
import com.pg.backend.security.LoginAttemptService;
import com.pg.backend.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    // Obtener todos los usuarios (Rol ADMIN requerido por SecurityFilter)
    @GetMapping
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Crear o actualizar un usuario (Rol ADMIN requerido por SecurityFilter)
    @PostMapping
    public ResponseEntity<?> guardarUsuario(@RequestBody Usuario usuario) {
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre de usuario es obligatorio.");
        }

        String cleanUsername = usuario.getUsername().trim().toLowerCase();
        usuario.setUsername(cleanUsername);

        // Si es un usuario nuevo
        if (usuario.getId() == null) {
            Optional<Usuario> existente = usuarioRepository.findByUsername(cleanUsername);
            if (existente.isPresent()) {
                return ResponseEntity.badRequest().body("El nombre de usuario ya está registrado.");
            }

            if (usuario.getPassword() == null || usuario.getPassword().trim().length() < 4) {
                return ResponseEntity.badRequest().body("La contraseña debe tener al menos 4 caracteres.");
            }

            // Hashear contraseña con BCrypt
            usuario.setPassword(BCrypt.hashpw(usuario.getPassword().trim(), BCrypt.gensalt(12)));
        } else {
            // Usuario existente que se está editando
            Optional<Usuario> actualOpt = usuarioRepository.findById(usuario.getId());
            if (actualOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Usuario actual = actualOpt.get();

            // Si se suministra una nueva contraseña, la hasheamos; si viene vacía, mantenemos la anterior
            if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
                if (usuario.getPassword().trim().length() < 4) {
                    return ResponseEntity.badRequest().body("La contraseña debe tener al menos 4 caracteres.");
                }
                usuario.setPassword(BCrypt.hashpw(usuario.getPassword().trim(), BCrypt.gensalt(12)));
            } else {
                usuario.setPassword(actual.getPassword());
            }
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(guardado);
    }

    // Eliminar un usuario (Rol ADMIN requerido por SecurityFilter)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id, HttpServletRequest request) {
        try {
            TokenService.TokenClaims claims = (TokenService.TokenClaims) request.getAttribute("authenticatedUser");
            if (claims != null && claims.getUserId() != null && claims.getUserId().equals(id)) {
                return ResponseEntity.badRequest().body("No puedes eliminar tu propio usuario de sesión activa.");
            }

            usuarioRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al intentar eliminar el usuario.");
        }
    }

    // Endpoint de login (público) con protección de fuerza bruta y BCrypt
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario y contraseña requeridos.");
        }

        String cleanUser = loginRequest.getUsername().trim().toLowerCase();
        String cleanPass = loginRequest.getPassword().trim();

        // 1. Verificar si está bloqueado por demasiados intentos fallidos (Anti-Brute Force)
        if (loginAttemptService.isBlocked(clientIp, cleanUser)) {
            long remaining = loginAttemptService.getRemainingLockoutMinutes(clientIp, cleanUser);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Demasiados intentos fallidos. Por seguridad, el acceso está bloqueado temporalmente durante " + remaining + " minuto(s).");
        }

        // 2. Buscar usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(cleanUser);
        if (usuarioOpt.isEmpty()) {
            loginAttemptService.loginFailed(clientIp, cleanUser);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
        }

        Usuario usuario = usuarioOpt.get();
        boolean passwordMatches = false;

        // 3. Comprobar contraseña (BCrypt o actualización transparente de contraseñas existentes en texto plano)
        String storedPass = usuario.getPassword();
        if (storedPass != null && (storedPass.startsWith("$2a$") || storedPass.startsWith("$2b$") || storedPass.startsWith("$2y$"))) {
            passwordMatches = BCrypt.checkpw(cleanPass, storedPass);
        } else if (storedPass != null && storedPass.equals(cleanPass)) {
            // Contraseña heredada en texto plano: coincide, la migramos automáticamente a BCrypt
            passwordMatches = true;
            usuario.setPassword(BCrypt.hashpw(cleanPass, BCrypt.gensalt(12)));
            usuarioRepository.save(usuario);
        }

        if (!passwordMatches) {
            loginAttemptService.loginFailed(clientIp, cleanUser);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
        }

        // 4. Login correcto: resetear contador de intentos
        loginAttemptService.loginSucceeded(clientIp, cleanUser);

        // 5. Generar Token JWT seguro
        String token = tokenService.generateToken(usuario.getId(), usuario.getUsername(), usuario.getRol());

        // 6. Devolver respuesta segura SIN contraseña
        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("username", usuario.getUsername());
        response.put("nombre", usuario.getNombre());
        response.put("rol", usuario.getRol());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    // Endpoint para verificar estado de sesión actual
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        TokenService.TokenClaims claims = (TokenService.TokenClaims) request.getAttribute("authenticatedUser");
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(claims.getUserId());
        return usuarioOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
