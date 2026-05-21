package com.example.claro.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.claro.model.Usuario;
import com.example.claro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión de usuarios")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está registrado.");
        }
        if (usuarioRepository.findByDocumento(usuario.getDocumento()).isPresent()) {
            return ResponseEntity.badRequest().body("El número de documento ya está registrado.");
        }

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (usuario.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(usuario);
            }
        }
        return ResponseEntity.status(401).body("Credenciales inválidas. Verifica tu correo y contraseña.");
    }
}
