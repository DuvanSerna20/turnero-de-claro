package com.example.claro.repository;

import com.example.claro.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para departamentos.
 * Usa findAll() del controlador para listar áreas del turnero.
 */
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
}
