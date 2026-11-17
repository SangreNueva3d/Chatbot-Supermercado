package com.udea.supermercado_chatbot.service;

import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.udea.supermercado_chatbot.agent.SupermercadoAgent;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private final InMemoryRunner runner = new InMemoryRunner(SupermercadoAgent.ROOT_AGENT);
    private final Map<String, String> sesiones = new ConcurrentHashMap<>();

    public String conversar(String userId, String mensaje) {
        String sessionId = sesiones.computeIfAbsent(userId, id -> {
            Session s = runner.sessionService().createSession(runner.appName(), id).blockingGet();
            return s.id();
        });

        Content contenido = Content.fromParts(Part.fromText(mensaje));
        StringBuilder respuesta = new StringBuilder();
        runner.runAsync(userId, sessionId, contenido).blockingForEach(event -> {
            if (event.finalResponse()) {
                respuesta.append(event.stringifyContent());
            }
        });
        return respuesta.toString();
    }
}