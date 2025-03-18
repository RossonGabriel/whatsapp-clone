package com.quangntn.whatsappclone.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quangntn.whatsappclone.message.Message;
import com.quangntn.whatsappclone.message.MessageState;
import com.quangntn.whatsappclone.message.MessageType;
import com.quangntn.whatsappclone.user.User;

class ChatMapperTest {

    private ChatMapper chatMapper;
    private Chat chat;
    private User sender;
    private User recipient;
    private static final String SENDER_ID = "sender123";
    private static final String RECIPIENT_ID = "recipient456";
    private static final String CHAT_ID = "chat789";
    private static final String SENDER_FIRST_NAME = "John";
    private static final String SENDER_LAST_NAME = "Doe";
    private static final String RECIPIENT_FIRST_NAME = "Jane";
    private static final String RECIPIENT_LAST_NAME = "Smith";

    @BeforeEach
    void setUp() {
        chatMapper = new ChatMapper();
        
        // Setup sender
        sender = new User();
        sender.setId(SENDER_ID);
        sender.setFirstName(SENDER_FIRST_NAME);
        sender.setLastName(SENDER_LAST_NAME);
        sender.setLastSeen(LocalDateTime.now());
        
        // Setup recipient
        recipient = new User();
        recipient.setId(RECIPIENT_ID);
        recipient.setFirstName(RECIPIENT_FIRST_NAME);
        recipient.setLastName(RECIPIENT_LAST_NAME);
        recipient.setLastSeen(LocalDateTime.now());
        
        // Setup chat
        chat = new Chat();
        chat.setId(CHAT_ID);
        chat.setSender(sender);
        chat.setRecipient(recipient);
        chat.setMessages(new ArrayList<>());
    }

    @Test
    void toChatResponse_WhenViewedBySender_ShouldShowRecipientName() {
        // When
        ChatResponse response = chatMapper.toChatResponse(chat, SENDER_ID);

        // Then
        assertEquals(CHAT_ID, response.getId());
        assertEquals(RECIPIENT_FIRST_NAME + " " + RECIPIENT_LAST_NAME, response.getName());
        assertEquals(0, response.getUnreadCount());
        assertNull(response.getLastMessage());
        assertTrue(response.isRecipientOnline());
        assertEquals(SENDER_ID, response.getSenderId());
        assertEquals(RECIPIENT_ID, response.getRecipientId());
        assertNull(response.getLastMessageTime());
    }

    @Test
    void toChatResponse_WhenViewedByRecipient_ShouldShowSenderName() {
        // When
        ChatResponse response = chatMapper.toChatResponse(chat, RECIPIENT_ID);

        // Then
        assertEquals(CHAT_ID, response.getId());
        assertEquals(SENDER_FIRST_NAME + " " + SENDER_LAST_NAME, response.getName());
        assertEquals(0, response.getUnreadCount());
        assertNull(response.getLastMessage());
        assertTrue(response.isRecipientOnline());
        assertEquals(SENDER_ID, response.getSenderId());
        assertEquals(RECIPIENT_ID, response.getRecipientId());
        assertNull(response.getLastMessageTime());
    }

    @Test
    void toChatResponse_WithMessages_ShouldShowLastMessageAndTime() {
        // Given
        LocalDateTime messageTime = LocalDateTime.now();
        Message message = new Message();
        message.setContent("Hello!");
        message.setType(MessageType.TEXT);
        message.setState(MessageState.SENT);
        message.setCreatedDate(messageTime);
        message.setRecipientId(SENDER_ID);
        chat.getMessages().add(message);

        // When
        ChatResponse response = chatMapper.toChatResponse(chat, SENDER_ID);

        // Then
        assertEquals("Hello!", response.getLastMessage());
        assertEquals(messageTime, response.getLastMessageTime());
    }

    @Test
    void toChatResponse_WithUnreadMessages_ShouldShowCorrectCount() {
        // Given
        Message message1 = new Message();
        message1.setState(MessageState.SENT);
        message1.setRecipientId(SENDER_ID);

        Message message2 = new Message();
        message2.setState(MessageState.SENT);
        message2.setRecipientId(SENDER_ID);

        Message message3 = new Message();
        message3.setState(MessageState.SEEN);
        message3.setRecipientId(SENDER_ID);

        chat.setMessages(List.of(message1, message2, message3));

        // When
        ChatResponse response = chatMapper.toChatResponse(chat, SENDER_ID);

        // Then
        assertEquals(2, response.getUnreadCount());
    }

    @Test
    void toChatResponse_WithNonTextMessage_ShouldShowAttachmentAsLastMessage() {
        // Given
        Message message = new Message();
        message.setType(MessageType.IMAGE);
        message.setRecipientId(SENDER_ID);
        chat.setMessages(List.of(message));

        // When
        ChatResponse response = chatMapper.toChatResponse(chat, SENDER_ID);

        // Then
        assertEquals("Attachment", response.getLastMessage());
    }

    @Test
    void toChatResponse_WithOfflineRecipient_ShouldShowOfflineStatus() {
        // Given
        recipient.setLastSeen(LocalDateTime.now().minusMinutes(10));

        // When
        ChatResponse response = chatMapper.toChatResponse(chat, SENDER_ID);

        // Then
        assertFalse(response.isRecipientOnline());
    }
} 