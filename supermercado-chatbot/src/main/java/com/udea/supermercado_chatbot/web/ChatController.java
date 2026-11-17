package com.udea.supermercado_chatbot.web;

import com.udea.supermercado_chatbot.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    public record ChatRequest(String userId, String mensaje) {}
    public record ChatResponse(String respuesta) {}

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest req) {
        try {
            return new ChatResponse(chatService.conversar(req.userId(), req.mensaje()));
        } catch (Exception e) {
            return new ChatResponse("Ocurrió un error: " + e.getMessage());
        }
    }
}