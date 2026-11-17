package com.udea.supermercado_mcp.repository;

import com.udea.supermercado_mcp.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String texto);
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    @Query("select distinct p.categoria from Producto p order by p.categoria")
    List<String> listarCategorias();
}