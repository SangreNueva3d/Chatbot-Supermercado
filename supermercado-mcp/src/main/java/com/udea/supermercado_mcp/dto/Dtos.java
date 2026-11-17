package com.udea.supermercado_mcp.dto;

import java.math.BigDecimal;
import java.util.List;

public final class Dtos {
    private Dtos() {}

    public record ProductoDto(Long id, String nombre, String categoria, BigDecimal precio, Integer stock) {}
    public record ClienteDto(Long id, String nombre, String telefono, String direccion) {}
    public record ItemDto(Long productoId, String producto, Integer cantidad,
                          BigDecimal precioUnitario, BigDecimal subtotal) {}
    public record PedidoDto(Long id, String estado, String cliente, String telefono,
                            String direccionEntrega, List<ItemDto> items, BigDecimal total) {}
}