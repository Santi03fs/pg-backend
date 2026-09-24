package com.pg.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Add Security Headers to all responses
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Always allow CORS preflight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Public endpoints that do not require authentication
        if (path.equals("/api/usuarios/login") || path.equals("/api/health") || !path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verify Bearer Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(request, response, HttpServletResponse.SC_UNAUTHORIZED, "No autorizado. Token de acceso requerido.");
            return;
        }

        String token = authHeader.substring(7).trim();
        TokenService.TokenClaims claims = tokenService.validateToken(token);

        if (claims == null) {
            sendError(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sesión inválida o expirada. Por favor vuelve a iniciar sesión.");
            return;
        }

        // Check ADMIN-only endpoints
        // Endpoints like /api/usuarios (list, create, delete users) require ADMIN role
        if (path.startsWith("/api/usuarios")) {
            if (!"ADMIN".equalsIgnoreCase(claims.getRole())) {
                sendError(request, response, HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: Se requiere rol de Administrador.");
                return;
            }
        }

        // Attach verified user claims to request for controllers
        request.setAttribute("authenticatedUser", claims);

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, int status, String message) throws IOException {
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isBlank()) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Vary", "Origin");
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"error\":\"%s\",\"status\":%d}", escapeJson(message), status));
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
