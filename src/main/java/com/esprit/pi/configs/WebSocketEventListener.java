package com.esprit.pi.configs;

import com.esprit.pi.dtos.ChatMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class WebSocketEventListener {

    public static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private Date getCurrentUtcDate() {
        return Date.from(LocalDateTime.now(ZoneId.of("UTC"))
                .atZone(ZoneId.systemDefault())

                .toInstant());
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Long teamId = (Long) headerAccessor.getSessionAttributes().get("teamId");

        if (username != null && teamId != null) {
            logger.info("User Disconnected : " + username + " from team: " + teamId);

            ChatMessageDTO chatMessage = new ChatMessageDTO();
            chatMessage.setType(ChatMessageDTO.MessageType.LEAVE);
            chatMessage.setSenderName(username);
            chatMessage.setCreatedAt(getCurrentUtcDate());
            chatMessage.setTeamMemberId(teamId);
            chatMessage.setMessageType(ChatMessageDTO.TeamDiscussionType.TEXT);
            chatMessage.setIsRead(true);

            messagingTemplate.convertAndSend("/topic/team/" + teamId, chatMessage);
        }
    }
}