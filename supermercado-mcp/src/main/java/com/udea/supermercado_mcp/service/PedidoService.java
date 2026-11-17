package com.udea.supermercado_mcp.service;

import com.udea.supermercado_mcp.dto.Dtos.*;
import com.udea.supermercado_mcp.model.*;
import com.udea.supermercado_mcp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PedidoService {

    private final ProductoRepository productos;
    private final ClienteRepository clientes;
    private final PedidoRepository pedidos;

    public PedidoService(ProductoRepository productos, ClienteRepository clientes, PedidoRepository pedidos) {
        this.productos = productos;
        this.clientes = clientes;
        this.pedidos = pedidos;
    }

    // ---------- Catálogo ----------
    @Transactional(readOnly = true)
    public List<ProductoDto> buscarProductos(String texto) {
        if (texto == null || texto.isBlank()) throw new IllegalArgumentException("Indique qué producto buscar");
        return productos.findByNombreContainingIgnoreCase(texto.trim()).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<String> listarCategorias() {
        return productos.listarCategorias();
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> productosPorCategoria(String categoria) {
        return productos.findByCategoriaIgnoreCase(categoria.trim()).stream().map(this::toDto).toList();
    }

    // ---------- Clientes ----------
    public ClienteDto registrarCliente(String nombre, String telefono, String direccion) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
        if (telefono == null || !telefono.trim().matches("\\d{7,15}"))
            throw new IllegalArgumentException("El teléfono debe tener solo dígitos (7 a 15)");
        Cliente c = clientes.findByTelefono(telefono.trim()).orElseGet(Cliente::new);
        c.setNombre(nombre.trim());
        c.setTelefono(telefono.trim());
        c.setDireccion(direccion == null ? null : direccion.trim());
        return toDto(clientes.save(c));
    }

    // ---------- Pedidos ----------
    public PedidoDto crearPedido(String telefono, String direccionEntrega) {
        Cliente c = clientes.findByTelefono(telefono.trim()).orElseThrow(() ->
                new IllegalArgumentException("No existe un cliente con teléfono " + telefono + ". Regístrelo primero."));
        String dir = (direccionEntrega == null || direccionEntrega.isBlank())
                ? c.getDireccion() : direccionEntrega.trim();
        if (dir == null || dir.isBlank()) throw new IllegalArgumentException("Se requiere una dirección de entrega");
        Pedido p = new Pedido();
        p.setCliente(c);
        p.setDireccionEntrega(dir);
        return toDto(pedidos.save(p));
    }

    public PedidoDto agregarProducto(Long pedidoId, Long productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        Pedido p = pedidoEnCarrito(pedidoId);
        Producto prod = productos.findById(productoId).orElseThrow(() ->
                new IllegalArgumentException("No existe el producto " + productoId));
        PedidoItem item = p.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId))
                .findFirst().orElse(null);
        int totalCantidad = cantidad + (item == null ? 0 : item.getCantidad());
        if (totalCantidad > prod.getStock())
            throw new IllegalArgumentException("Stock insuficiente de " + prod.getNombre()
                    + ". Disponible: " + prod.getStock());
        if (item == null) {
            item = new PedidoItem();
            item.setPedido(p);
            item.setProducto(prod);
            item.setPrecioUnitario(prod.getPrecio());
            p.getItems().add(item);
        }
        item.setCantidad(totalCantidad);
        return toDto(pedidos.save(p));
    }

    public PedidoDto quitarProducto(Long pedidoId, Long productoId) {
        Pedido p = pedidoEnCarrito(pedidoId);
        boolean quitado = p.getItems().removeIf(i -> i.getProducto().getId().equals(productoId));
        if (!quitado) throw new IllegalArgumentException("El producto " + productoId + " no está en el pedido");
        return toDto(pedidos.save(p));
    }

    @Transactional(readOnly = true)
    public PedidoDto verPedido(Long pedidoId) {
        return toDto(buscarPedido(pedidoId));
    }

    public PedidoDto confirmarPedido(Long pedidoId) {
        Pedido p = pedidoEnCarrito(pedidoId);
        if (p.getItems().isEmpty()) throw new IllegalArgumentException("El pedido está vacío");
        for (PedidoItem i : p.getItems()) {
            if (i.getCantidad() > i.getProducto().getStock())
                throw new IllegalArgumentException("Stock insuficiente de " + i.getProducto().getNombre());
        }
        for (PedidoItem i : p.getItems()) {
            Producto prod = i.getProducto();
            prod.setStock(prod.getStock() - i.getCantidad());
        }
        p.setEstado(EstadoPedido.CONFIRMADO);
        return toDto(p);
    }

    public PedidoDto cancelarPedido(Long pedidoId) {
        Pedido p = buscarPedido(pedidoId);
        if (p.getEstado() == EstadoPedido.CANCELADO) throw new IllegalArgumentException("El pedido ya estaba cancelado");
        if (p.getEstado() == EstadoPedido.CONFIRMADO) {
            for (PedidoItem i : p.getItems()) {
                Producto prod = i.getProducto();
                prod.setStock(prod.getStock() + i.getCantidad());
            }
        }
        p.setEstado(EstadoPedido.CANCELADO);
        return toDto(p);
    }

    @Transactional(readOnly = true)
    public List<PedidoDto> pedidosDeCliente(String telefono) {
        return pedidos.findByClienteTelefonoOrderByFechaDesc(telefono.trim()).stream().map(this::toDto).toList();
    }

    // ---------- Auxiliares ----------
    private Pedido buscarPedido(Long id) {
        return pedidos.findById(id).orElseThrow(() -> new IllegalArgumentException("No existe el pedido " + id));
    }

    private Pedido pedidoEnCarrito(Long id) {
        Pedido p = buscarPedido(id);
        if (p.getEstado() != EstadoPedido.CARRITO)
            throw new IllegalArgumentException("El pedido " + id + " está " + p.getEstado() + " y ya no se puede modificar");
        return p;
    }

    private ProductoDto toDto(Producto p) {
        return new ProductoDto(p.getId(), p.getNombre(), p.getCategoria(), p.getPrecio(), p.getStock());
    }

    private ClienteDto toDto(Cliente c) {
        return new ClienteDto(c.getId(), c.getNombre(), c.getTelefono(), c.getDireccion());
    }

    private PedidoDto toDto(Pedido p) {
        List<ItemDto> items = p.getItems().stream().map(i -> new ItemDto(
                i.getProducto().getId(), i.getProducto().getNombre(), i.getCantidad(), i.getPrecioUnitario(),
                i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))).toList();
        BigDecimal total = items.stream().map(ItemDto::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PedidoDto(p.getId(), p.getEstado().name(), p.getCliente().getNombre(),
                p.getCliente().getTelefono(), p.getDireccionEntrega(), items, total);
    }
}