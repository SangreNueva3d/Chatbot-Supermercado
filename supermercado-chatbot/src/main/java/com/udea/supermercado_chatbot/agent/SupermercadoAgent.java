package com.udea.supermercado_chatbot.agent;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.mcp.McpToolset;
import com.google.adk.tools.mcp.StreamableHttpServerParameters;
import java.time.Duration;
import java.util.List;

public class SupermercadoAgent {

    public static final String MCP_URL = "http://localhost:8088/mcp";

    public static BaseAgent ROOT_AGENT = buildAgent();

    private static BaseAgent buildAgent() {
        StreamableHttpServerParameters params = StreamableHttpServerParameters.builder()
                .url(MCP_URL)
                .build();

        McpToolset mcpToolset = new McpToolset(params);
        try {
            List<BaseTool> mcpTools = mcpToolset.getTools(null).toList().blockingGet();
            System.out.println("Herramientas MCP descubiertas: " + mcpTools.size());

            return LlmAgent.builder()
                    .name("SupermercadoAgent")
                    .model("gemini-3.8-flash")
                    .description("Asistente de pedidos a domicilio de un supermercado")
                    .instruction(
                            "Eres SuperBot, el asistente de un supermercado con servicio a domicilio. "
                                    + "Habla en español, de forma amable y breve. Los precios están en pesos colombianos (COP). "
                                    + "Usa SIEMPRE las herramientas MCP. Nunca inventes productos, precios, stock ni ids.\n\n"
                                    + "Flujo de atención:\n"
                                    + "1) Pide el teléfono del cliente. Si no está registrado, pide nombre y dirección y usa registrarCliente.\n"
                                    + "2) Ayuda a buscar productos con buscarProductos, listarCategorias o listarProductosPorCategoria.\n"
                                    + "3) Crea el pedido con crearPedido y agrega productos con agregarProducto.\n"
                                    + "4) Antes de confirmar, muestra un resumen con verPedido (productos, cantidades, total y dirección) y pide confirmación explícita.\n"
                                    + "5) Solo después del sí del cliente llama confirmarPedido.\n"
                                    + "Antes de cancelar un pedido (cancelarPedido) pide confirmación.\n"
                                    + "Si una herramienta devuelve un error, explícalo con claridad y propón una alternativa.")
                    .tools(mcpTools)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible conectar con el servidor MCP en " + MCP_URL
                    + ". ¿Está corriendo supermercado-mcp?", e);
        }
    }
}