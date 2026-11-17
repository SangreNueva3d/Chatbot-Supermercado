package com.udea.supermercado_mcp;

import com.udea.supermercado_mcp.tools.SupermercadoTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SupermercadoMcpApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupermercadoMcpApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider supermercadoToolCallbacks(SupermercadoTools tools) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(tools)
				.build();
	}
}