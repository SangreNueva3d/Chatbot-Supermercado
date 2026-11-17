package com.udea.supermercado_mcp.config;

import com.udea.supermercado_mcp.model.Producto;
import com.udea.supermercado_mcp.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductoRepository repo;

    public DataLoader(ProductoRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        repo.saveAll(List.of(
                new Producto("Leche entera 1L", "Lácteos", new BigDecimal("4200"), 50),
                new Producto("Yogurt natural 1L", "Lácteos", new BigDecimal("7500"), 30),
                new Producto("Queso campesino 500g", "Lácteos", new BigDecimal("12900"), 20),
                new Producto("Huevos x30", "Lácteos", new BigDecimal("18500"), 25),
                new Producto("Manzana roja kg", "Frutas y verduras", new BigDecimal("6900"), 40),
                new Producto("Banano kg", "Frutas y verduras", new BigDecimal("3200"), 60),
                new Producto("Tomate chonto kg", "Frutas y verduras", new BigDecimal("4500"), 35),
                new Producto("Papa pastusa kg", "Frutas y verduras", new BigDecimal("3000"), 80),
                new Producto("Pan tajado integral", "Panadería", new BigDecimal("6200"), 40),
                new Producto("Arroz 1kg", "Despensa", new BigDecimal("4800"), 100),
                new Producto("Frijol cargamanto 500g", "Despensa", new BigDecimal("7900"), 45),
                new Producto("Aceite de girasol 1L", "Despensa", new BigDecimal("14500"), 30),
                new Producto("Pasta espagueti 500g", "Despensa", new BigDecimal("3900"), 70),
                new Producto("Pechuga de pollo kg", "Carnes", new BigDecimal("16900"), 25),
                new Producto("Carne molida de res 500g", "Carnes", new BigDecimal("13900"), 20),
                new Producto("Gaseosa cola 1.5L", "Bebidas", new BigDecimal("5600"), 60),
                new Producto("Jugo de naranja 1L", "Bebidas", new BigDecimal("6800"), 35),
                new Producto("Agua sin gas 600ml", "Bebidas", new BigDecimal("2200"), 120),
                new Producto("Detergente líquido 1L", "Aseo", new BigDecimal("15900"), 25),
                new Producto("Papel higiénico x12", "Aseo", new BigDecimal("21900"), 30)
        ));
    }
}