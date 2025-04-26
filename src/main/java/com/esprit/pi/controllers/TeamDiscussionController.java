package com.esprit.pi.controllers;

import com.esprit.pi.dtos.ChatMessageDTO;
import com.esprit.pi.entities.TeamDiscussion;
import com.esprit.pi.entities.TeamMembers;
import com.esprit.pi.services.ITeamDiscussionService;
import com.esprit.pi.services.ITeamMembersService;
import com.esprit.pi.services.JwtUtility;
import com.esprit.pi.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.esprit.pi.configs.WebSocketEventListener.logger;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/team-discussions")
public class TeamDiscussionController {

    private final ITeamDiscussionService teamDiscussionService;
    private final ITeamMembersService teamMembersService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    public TeamDiscussionController(ITeamDiscussionService teamDiscussionService,
                                    ITeamMembersService teamMembersService,
                                    SimpMessagingTemplate messagingTemplate,
                                    JwtUtility jwtUtility,
                                    UserRepository userRepository) {
        this.teamDiscussionService = teamDiscussionService;
        this.teamMembersService = teamMembersService;
        this.messagingTemplate = messagingTemplate;
        this.jwtUtility = jwtUtility;
        this.userRepository = userRepository;
    }

    // --- FILE UPLOAD ---
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teamMemberId") Long teamMemberId,
            @RequestParam("teamId") Long teamId
    ) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "http://localhost:9100/uploads/" + fileName;
        TeamDiscussion discussion = teamDiscussionService.createDiscussion(
                teamMemberId, fileUrl, ChatMessageDTO.TeamDiscussionType.FILE
        );

        return ResponseEntity.ok(discussion);
    }

    // --- SEND MESSAGE REST ---
    @PostMapping("/send/{teamId}")
    public ResponseEntity<?> sendMessageRest(
            @PathVariable Long teamId,
            @RequestParam Long teamMemberId,
            @RequestParam(defaultValue = "TEXT") ChatMessageDTO.TeamDiscussionType messageType,
            @RequestBody Map<String, String> payload) {
        logger.info("Entering sendMessageRest: teamId={}, teamMemberId={}", teamId, teamMemberId);

        try {
            if (payload == null || !payload.containsKey("message")) {
                logger.warn("Invalid payload: {}", payload);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Message is required in payload"));
            }

            TeamMembers teamMember = teamMembersService.findTeamMemberById(teamMemberId);
            if (teamMember.getUser().getUsername() == null) {
                logger.error("User for teamMemberId={} has null username", teamMemberId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid user data: username is missing"));
            }
            if (!teamMember.getTeam().getId().equals(teamId)) {
                logger.error("Team membership validation failed: teamMemberId={}, memberTeamId={}, requestedTeamId={}",
                        teamMemberId, teamMember.getTeam().getId(), teamId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "User is not a member of this team"));
            }

            String message = payload.get("message").trim();
            if (message.isEmpty()) {
                logger.warn("Message is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Message content is required"));
            }

            TeamDiscussion discussion = teamDiscussionService.createDiscussion(teamMemberId, message, messageType);
            ChatMessageDTO chatMessage = buildChatMessageDTO(discussion, teamMember.getUser().getUsername());
            messagingTemplate.convertAndSend("/topic/team/" + teamId, chatMessage);
            logger.info("Message sent to WebSocket: /topic/team/{}", teamId);

            return ResponseEntity.ok(chatMessage);
        } catch (Exception e) {
            logger.error("Unexpected error in sendMessageRest: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send message: " + e.getMessage()));
        }
    }

    // --- TYPING NOTIFICATION REST ENDPOINT ---
    @PostMapping("/{teamId}/typing")
    public ResponseEntity<?> typingNotification(
            @PathVariable Long teamId,
            @RequestParam Long teamMemberId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            logger.info("Received typing notification: teamId={}, teamMemberId={}, AuthorizationHeaderPresent={}", teamId, teamMemberId, authorizationHeader != null);
            // Parameter validation
            if (teamMemberId == null) {
                logger.warn("Missing teamMemberId in typing notification request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "teamMemberId is required"));
            }
            // Optional: JWT validation
            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
                if (jwtUtility.isTokenExpired(token)) {
                    logger.warn("Token expired for typing notification: teamMemberId={}, teamId={}", teamMemberId, teamId);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "Token expired"));
                }
            } else {
                logger.info("No Authorization header provided for typing notification (allowed, but not recommended)");
            }
            // Validate team membership
            TeamMembers teamMember = teamMembersService.findTeamMemberById(teamMemberId);
            if (teamMember == null) {
                logger.warn("No TeamMember found for teamMemberId={}", teamMemberId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid teamMemberId: not found"));
            }
            if (!teamMember.getTeam().getId().equals(teamId)) {
                logger.warn("Team membership validation failed: teamMemberId={}, memberTeamId={}, requestedTeamId={}",
                        teamMemberId, teamMember.getTeam().getId(), teamId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "User is not a member of this team"));
            }
            // Broadcast typing event via WebSocket
            ChatMessageDTO typingMsg = new ChatMessageDTO();
            typingMsg.setType(ChatMessageDTO.MessageType.TYPING);
            typingMsg.setSenderName(teamMember.getUser().getUsername());
            typingMsg.setTeamMemberId(teamMemberId);
            typingMsg.setUserId(teamMember.getUser().getId());
            typingMsg.setCreatedAt(new java.util.Date());
            typingMsg.setMessageType(ChatMessageDTO.TeamDiscussionType.TEXT);
            messagingTemplate.convertAndSend("/topic/team/" + teamId + "/typing", typingMsg);
            logger.info("Typing notification broadcasted to /topic/team/{}/typing by user {} (teamMemberId={})", teamId, typingMsg.getSenderName(), teamMemberId);
            return ResponseEntity.ok(Map.of(
                    "message", "Typing notification received",
                    "timestamp", new java.util.Date().toString()
            ));
        } catch (Exception e) {
            logger.error("Failed to process typing notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process typing notification: " + e.getMessage()));
        }
    }

    // --- INFO, GET, PARTICIPANTS, MARK AS READ, DELETE, UNREAD COUNT ---

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getChatInfo(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
            if (jwtUtility.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token expired"));
            }
            String username = jwtUtility.getUserName(token);
            return ResponseEntity.ok(Map.of(
                    "currentDateTime", new Date().toString(),
                    "currentUser", username
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get chat info: " + e.getMessage()));
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamDiscussion>> getTeamDiscussions(@PathVariable Long teamId) {
        try {
            List<TeamDiscussion> discussions = teamDiscussionService.getTeamDiscussions(teamId);
            return ResponseEntity.ok(discussions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/participants/{teamId}")
    public ResponseEntity<Map<String, Object>> getParticipants(@PathVariable Long teamId) {
        List<TeamMembers> members = teamMembersService.findMembersByTeamId(teamId);
        List<String> usernames = members.stream()
                .map(tm -> tm.getUser().getUsername())
                .toList();
        return ResponseEntity.ok(Map.of(
                "participants", usernames,
                "timestamp", new Date().toString()
        ));
    }

    @PutMapping("/{discussionId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long discussionId) {
        try {
            TeamDiscussion discussion = teamDiscussionService.markAsRead(discussionId);
            return ResponseEntity.ok(Map.of(
                    "message", "Message marked as read",
                    "timestamp", new Date().toString(),
                    "discussionId", discussionId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to mark message as read: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{discussionId}")
    public ResponseEntity<?> deleteDiscussion(@PathVariable Long discussionId) {
        try {
            TeamDiscussion discussion = teamDiscussionService.getDiscussionById(discussionId);
            Long teamId = discussion.getTeam().getId();
            teamDiscussionService.deleteDiscussion(discussionId);

            ChatMessageDTO chatMessage = new ChatMessageDTO();
            chatMessage.setId(discussionId);
            chatMessage.setType(ChatMessageDTO.MessageType.LEAVE);
            chatMessage.setSenderName(discussion.getTeamMember().getUser().getUsername());
            chatMessage.setMessage("Message deleted");
            chatMessage.setCreatedAt(new Date());
            chatMessage.setIsRead(true);
            messagingTemplate.convertAndSend("/topic/team/" + teamId, chatMessage);

            return ResponseEntity.ok(Map.of(
                    "message", "Discussion deleted successfully",
                    "timestamp", new Date().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete discussion: " + e.getMessage()));
        }
    }

    @GetMapping("/unread/count/{teamId}")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable Long teamId) {
        try {
            Long count = teamDiscussionService.getUnreadCount(teamId);
            return ResponseEntity.ok(Map.of(
                    "unreadCount", count,
                    "timestamp", new Date().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get unread count: " + e.getMessage()));
        }
    }

    // --- WEBSOCKET MESSAGE HANDLERS ---

    @MessageMapping("/chat.sendMessage/{teamId}")
    @SendTo("/topic/team/{teamId}")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessage,
                                      @DestinationVariable Long teamId) {
        if (chatMessage.getTeamMemberId() == null) {
            return errorMessage("Team member ID is required");
        }
        TeamMembers teamMember = teamMembersService.findTeamMemberById(chatMessage.getTeamMemberId());
        if (teamMember.getUser().getUsername() == null) {
            return errorMessage("Invalid user data: username is missing");
        }
        if (!teamMember.getTeam().getId().equals(teamId)) {
            return errorMessage("User is not a member of this team");
        }

        TeamDiscussion discussion = teamDiscussionService.createDiscussion(
                chatMessage.getTeamMemberId(),
                chatMessage.getMessage(),
                chatMessage.getMessageType()
        );
        return buildChatMessageDTO(discussion, teamMember.getUser().getUsername());
    }

    @MessageMapping("/chat.addUser/{teamId}")
    @SendTo("/topic/team/{teamId}")
    public ChatMessageDTO addUser(
            @Payload ChatMessageDTO chatMessage,
            @DestinationVariable Long teamId,
            SimpMessageHeaderAccessor headerAccessor) {
        if (chatMessage.getTeamMemberId() == null) {
            return errorMessage("Team member ID is required");
        }
        TeamMembers teamMember = teamMembersService.findTeamMemberById(chatMessage.getTeamMemberId());
        if (teamMember.getUser().getUsername() == null) {
            return errorMessage("Invalid user data: username is missing");
        }
        if (!teamMember.getTeam().getId().equals(teamId)) {
            return errorMessage("User is not a member of this team");
        }

        String username = teamMember.getUser().getUsername();
        headerAccessor.getSessionAttributes().put("username", username);
        headerAccessor.getSessionAttributes().put("teamId", teamId);

        ChatMessageDTO joinMessage = new ChatMessageDTO();
        joinMessage.setType(ChatMessageDTO.MessageType.JOIN);
        joinMessage.setSenderName(username);
        joinMessage.setMessage(username + " joined the conversation");
        joinMessage.setTeamMemberId(chatMessage.getTeamMemberId());
        joinMessage.setUserId(teamMember.getUser().getId());
        joinMessage.setCreatedAt(new Date());
        joinMessage.setMessageType(ChatMessageDTO.TeamDiscussionType.TEXT);
        joinMessage.setIsRead(true);
        return joinMessage;
    }

    // --- WEBSOCKET TYPING HANDLER ---
    @MessageMapping("/chat.typing/{teamId}")
    @SendTo("/topic/team/{teamId}/typing")
    public ChatMessageDTO typingWebSocket(@Payload ChatMessageDTO chatMessage, @DestinationVariable Long teamId) {
        if (chatMessage.getTeamMemberId() == null) {
            return errorMessage("Team member ID is required");
        }
        TeamMembers teamMember = teamMembersService.findTeamMemberById(chatMessage.getTeamMemberId());
        if (teamMember == null || teamMember.getUser().getUsername() == null) {
            return errorMessage("Invalid user data: username is missing");
        }
        if (!teamMember.getTeam().getId().equals(teamId)) {
            return errorMessage("User is not a member of this team");
        }
        chatMessage.setType(ChatMessageDTO.MessageType.TYPING);
        chatMessage.setSenderName(teamMember.getUser().getUsername());
        chatMessage.setUserId(teamMember.getUser().getId());
        chatMessage.setCreatedAt(new Date());
        chatMessage.setMessageType(ChatMessageDTO.TeamDiscussionType.TEXT);
        return chatMessage;
    }

    // --- HELPERS ---
    private ChatMessageDTO buildChatMessageDTO(TeamDiscussion discussion, String senderName) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(discussion.getId());
        dto.setType(ChatMessageDTO.MessageType.CHAT);
        dto.setMessage(discussion.getMessage());
        dto.setTeamMemberId(discussion.getTeamMember().getId());
        dto.setUserId(discussion.getTeamMember().getUser().getId());
        dto.setSenderName(senderName);
        dto.setCreatedAt(discussion.getCreatedAt());
        dto.setIsRead(discussion.getIsRead());
        dto.setMessageType(convertToDtoMessageType(discussion.getMessageType()));
        return dto;
    }

    private ChatMessageDTO errorMessage(String error) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setType(ChatMessageDTO.MessageType.ERROR);
        dto.setMessage(error);
        dto.setSenderName("System");
        dto.setCreatedAt(new Date());
        dto.setIsRead(true);
        return dto;
    }

    private ChatMessageDTO.TeamDiscussionType convertToDtoMessageType(TeamDiscussion.MessageType entityType) {
        if (entityType == null) return ChatMessageDTO.TeamDiscussionType.TEXT;
        return switch (entityType) {
            case TEXT -> ChatMessageDTO.TeamDiscussionType.TEXT;
            case IMAGE -> ChatMessageDTO.TeamDiscussionType.IMAGE;
            case FILE -> ChatMessageDTO.TeamDiscussionType.FILE;
            case EMOJI -> ChatMessageDTO.TeamDiscussionType.EMOJI;
        };
    }
}