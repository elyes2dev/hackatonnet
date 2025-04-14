package com.esprit.pi.controllers;

import com.esprit.pi.dtos.ChatMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket-test")
@Tag(name = "WebSocket Test Controller", description = "Endpoints for testing WebSocket functionality")
@CrossOrigin("*")
public class WebSocketTestController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "Send test message to WebSocket topic")
    @PostMapping("/send/{teamId}")
    public ResponseEntity<Map<String, Object>> sendTestMessage(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "Test User") String senderName,
            @RequestParam(defaultValue = "Hello, WebSocket!") String message) {

        ChatMessageDTO chatMessage = new ChatMessageDTO();
        chatMessage.setType(ChatMessageDTO.MessageType.CHAT);
        chatMessage.setSenderName(senderName);
        chatMessage.setMessage(message);
        chatMessage.setCreatedAt(new Date());
        chatMessage.setTeamMemberId(teamId);
        chatMessage.setMessageType(ChatMessageDTO.TeamDiscussionType.TEXT);
        chatMessage.setIsRead(false);

        messagingTemplate.convertAndSend("/topic/team/" + teamId, chatMessage);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Message sent");
        response.put("timestamp", new Date());
        response.put("teamId", teamId);
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Test WebSocket connection status")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getWebSocketStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "WebSocket server is running");
        status.put("timestamp", new Date());
        status.put("endpoints", Map.of(
                "chat", "/topic/team/{teamId}",
                "user", "/user/queue/reply"
        ));

        return ResponseEntity.ok(status);
    }
}