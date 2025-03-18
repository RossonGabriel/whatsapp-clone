package com.quangntn.whatsappclone.chat;

import org.springframework.stereotype.Service;

@Service
public class ChatMapper {
    public ChatResponse toChatResponse(Chat chat, String senderId) {
        return ChatResponse.builder()
                .id(chat.getId())
                .name(chat.getChatName(senderId))
                .unreadCount(chat.getUnreadMessages(senderId))
                .lastMessage(chat.getLastMessage())
                .isRecipientOnline(this.isRecipientOnline(chat, senderId))
                .senderId(chat.getSender().getId())
                .recipientId(chat.getRecipient().getId())
                .lastMessageTime(chat.getLastMessageTime())
                .build();
    }

    private boolean isRecipientOnline(Chat chat, String senderId) {
        if (!chat.getRecipient().getId().equals(senderId)) {
            return chat.getRecipient().isUserOnline();
        }
        return chat.getSender().isUserOnline();
    }
}

