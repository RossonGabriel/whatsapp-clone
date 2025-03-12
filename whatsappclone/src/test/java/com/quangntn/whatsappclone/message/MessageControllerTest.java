package com.quangntn.whatsappclone.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveMessage_ShouldCallService() {
        // Given
        MessageRequest messageRequest = new MessageRequest();

        // When
        messageController.saveMessage(messageRequest);

        // Then
        verify(messageService, times(1)).saveMessage(messageRequest);
    }

    @Test
    void uploadMedia_ShouldCallService() {
        // Given
        String chatId = "chat123";
        MultipartFile file = mock(MultipartFile.class);
        Authentication authentication = mock(Authentication.class);

        // When
        messageController.uploadMedia(chatId, file, authentication);

        // Then
        verify(messageService, times(1)).uploadMediaMessage(chatId, file, authentication);
    }

    @Test
    void setMessageToSeen_ShouldCallService() {
        // Given
        String chatId = "chat123";
        Authentication authentication = mock(Authentication.class);

        // When
        messageController.setMessageToSeen(chatId, authentication);

        // Then
        verify(messageService, times(1)).setMessageToSeen(chatId, authentication);
    }

    @Test
    void getMessages_ShouldReturnMessageResponses() {
        // Given
        String chatId = "chat123";
        List<MessageResponse> messageResponses = List.of(new MessageResponse());
        when(messageService.findChatMessages(chatId)).thenReturn(messageResponses);

        // When
        ResponseEntity<List<MessageResponse>> response = messageController.getMessages(chatId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messageResponses, response.getBody());
        verify(messageService, times(1)).findChatMessages(chatId);
    }
} 