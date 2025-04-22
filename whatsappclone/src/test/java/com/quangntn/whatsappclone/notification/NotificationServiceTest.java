package com.quangntn.whatsappclone.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendNotification_ShouldSendMessage() {
        // Given
        String userId = "user123";

        // When
        notificationService.sendNotification(userId, notification);

        // Then
        verify(messagingTemplate, times(1)).convertAndSendToUser(
                userId,
                "/chat",
                notification
        );
    }
} 