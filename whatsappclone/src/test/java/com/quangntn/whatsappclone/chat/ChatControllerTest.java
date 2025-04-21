package com.quangntn.whatsappclone.chat;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.quangntn.whatsappclone.common.StringResponse;

class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateChat() {
        String senderId = "sender123";
        String recipientId = "recipient123";
        String expectedChatId = "chat123";

        when(chatService.createChat(senderId, recipientId)).thenReturn(expectedChatId);

        ResponseEntity<StringResponse> response = chatController.createChat(senderId, recipientId);

        assertEquals(expectedChatId, response.getBody().getResponse());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetChatsByRecipientId() {
        Authentication authentication = null; // Mock or create a suitable Authentication object
        List<ChatResponse> expectedChats = Collections.emptyList();

        when(chatService.getChatsByRecipientId(any(Authentication.class))).thenReturn(expectedChats);

        ResponseEntity<List<ChatResponse>> response = chatController.getChatsByRecipientId(authentication);

        assertEquals(expectedChats, response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }
} 