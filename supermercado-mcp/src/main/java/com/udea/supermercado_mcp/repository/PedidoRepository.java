package com.udea.supermercado_mcp.repository;

import com.udea.supermercado_mcp.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteTelefonoOrderByFechaDesc(String telefono);
}