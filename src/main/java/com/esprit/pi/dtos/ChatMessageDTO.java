package com.esprit.pi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private MessageType type;
    private String message;
    private Long teamMemberId;
    private Long userId;
    private String senderName;
    private Date createdAt;
    private Boolean isRead;
    private TeamDiscussionType messageType;

    public enum MessageType {
        CHAT,
        JOIN,
        ERROR,
        LEAVE
    }

    public enum TeamDiscussionType {
        TEXT,
        IMAGE,
        FILE,
        EMOJI
    }

    // Manual getters and setters in case Lombok isn't working
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getTeamMemberId() { return teamMemberId; }
    public void setTeamMemberId(Long teamMemberId) { this.teamMemberId = teamMemberId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public TeamDiscussionType getMessageType() { return messageType; }
    public void setMessageType(TeamDiscussionType messageType) { this.messageType = messageType; }
}