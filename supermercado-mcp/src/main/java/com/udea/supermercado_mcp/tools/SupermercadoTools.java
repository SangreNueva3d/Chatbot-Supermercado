package com.udea.supermercado_mcp.tools;

import com.udea.supermercado_mcp.dto.Dtos.*;
import com.udea.supermercado_mcp.service.PedidoService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SupermercadoTools {

    private final PedidoService service;

    public SupermercadoTools(PedidoService service) {
        this.service = service;
    }

    @Tool(description = "Busca productos del supermercado por fragmento del nombre. Devuelve id, nombre, categoría, precio en COP y stock.")
    public List<ProductoDto> buscarProductos(
            @ToolParam(description = "Fragmento del nombre, por ejemplo 'leche'") String texto) {
        return service.buscarProductos(texto);
    }

    @Tool(description = "Lista las categorías de productos disponibles en el supermercado.")
    public List<String> listarCategorias() {
        return service.listarCategorias();
    }

    @Tool(description = "Lista los productos de una categoría.")
    public List<ProductoDto> listarProductosPorCategoria(
            @ToolParam(description = "Nombre de la categoría, por ejemplo 'Lácteos'") String categoria) {
        return service.productosPorCategoria(categoria);
    }

    @Tool(description = "Registra un cliente nuevo o actualiza sus datos si el teléfono ya existe.")
    public ClienteDto registrarCliente(
            @ToolParam(description = "Nombre completo") String nombre,
            @ToolParam(description = "Teléfono, solo dígitos") String telefono,
            @ToolParam(description = "Dirección de domicilio") String direccion) {
        return service.registrarCliente(nombre, telefono, direccion);
    }

    @Tool(description = "Crea un pedido nuevo (estado CARRITO) para un cliente ya registrado. Devuelve el id del pedido.")
    public PedidoDto crearPedido(
            @ToolParam(description = "Teléfono del cliente registrado") String telefono,
            @ToolParam(description = "Dirección de entrega; si no se indica se usa la del cliente", required = false)
            String direccionEntrega) {
        return service.crearPedido(telefono, direccionEntrega);
    }

    @Tool(description = "Agrega un producto con su cantidad a un pedido en estado CARRITO.")
    public PedidoDto agregarProducto(
            @ToolParam(description = "Id del pedido") Long pedidoId,
            @ToolParam(description = "Id del producto") Long productoId,
            @ToolParam(description = "Cantidad, mayor a 0") Integer cantidad) {
        return service.agregarProducto(pedidoId, productoId, cantidad);
    }

    @Tool(description = "Quita un producto de un pedido en estado CARRITO.")
    public PedidoDto quitarProducto(
            @ToolParam(description = "Id del pedido") Long pedidoId,
            @ToolParam(description = "Id del producto a quitar") Long productoId) {
        return service.quitarProducto(pedidoId, productoId);
    }

    @Tool(description = "Muestra el detalle de un pedido: estado, productos, cantidades y total en COP.")
    public PedidoDto verPedido(@ToolParam(description = "Id del pedido") Long pedidoId) {
        return service.verPedido(pedidoId);
    }

    @Tool(description = "Confirma el pedido para domicilio y descuenta el stock. Solo llamar cuando el cliente lo confirme explícitamente.")
    public PedidoDto confirmarPedido(@ToolParam(description = "Id del pedido") Long pedidoId) {
        return service.confirmarPedido(pedidoId);
    }

    @Tool(description = "Cancela un pedido y devuelve el stock si estaba confirmado. Pedir confirmación al cliente antes de usarla.")
    public PedidoDto cancelarPedido(@ToolParam(description = "Id del pedido") Long pedidoId) {
        return service.cancelarPedido(pedidoId);
    }

    @Tool(description = "Lista el historial de pedidos de un cliente por su teléfono.")
    public List<PedidoDto> consultarPedidosCliente(
            @ToolParam(description = "Teléfono del cliente") String telefono) {
        return service.pedidosDeCliente(telefono);
    }
}