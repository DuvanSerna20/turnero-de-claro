package com.example.claro.repository;

import com.example.claro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario.
 * Spring implementa automáticamente los métodos de consulta a MySQL.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Busca usuario por email (login y validación de registro) */
    Optional<Usuario> findByEmail(String email);

    /** Busca usuario por documento (evitar duplicados al registrarse) */
    Optional<Usuario> findByDocumento(String documento);
}
