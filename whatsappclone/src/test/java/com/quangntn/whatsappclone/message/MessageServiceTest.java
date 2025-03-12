package com.quangntn.whatsappclone.message;

import com.quangntn.whatsappclone.chat.Chat;
import com.quangntn.whatsappclone.chat.ChatRepository;
import com.quangntn.whatsappclone.file.FileService;
import com.quangntn.whatsappclone.notification.Notification;
import com.quangntn.whatsappclone.notification.NotificationService;
import com.quangntn.whatsappclone.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageMapper mapper;

    @Mock
    private FileService fileService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveMessage_ShouldSaveMessageAndSendNotification() {
        // Given
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setChatId("chat123");
        messageRequest.setContent("Hello");
        messageRequest.setSenderId("user1");
        messageRequest.setRecipientId("user2");
        messageRequest.setType(MessageType.TEXT);

        Chat chat = mock(Chat.class);
        when(chatRepository.findById("chat123")).thenReturn(java.util.Optional.of(chat));

        // When
        messageService.saveMessage(messageRequest);

        // Then
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(notificationService, times(1)).sendNotification(eq("user2"), any(Notification.class));
    }

    @Test
    void findChatMessages_ShouldReturnMessageResponses() {
        // Given
        String chatId = "chat123";
        List<Message> messages = List.of(new Message());
        when(messageRepository.findMessagesByChatId(chatId)).thenReturn(messages);

        MessageResponse messageResponse = new MessageResponse();
        when(mapper.toMessageResponse(any(Message.class))).thenReturn(messageResponse);

        // When
        List<MessageResponse> result = messageService.findChatMessages(chatId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(messageResponse, result.get(0));
        verify(messageRepository, times(1)).findMessagesByChatId(chatId);
        verify(mapper, times(1)).toMessageResponse(any(Message.class));
    }

    @Test
    void setMessageToSeen_ShouldSetMessagesToSeenAndSendNotification() {
        // Given
        String chatId = "chat123";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        User sender = new User();
        sender.setId("user1");
        User recipient = new User();
        recipient.setId("user2");

        Chat chat = mock(Chat.class);
        when(chat.getId()).thenReturn(chatId);
        when(chat.getSender()).thenReturn(sender);
        when(chat.getRecipient()).thenReturn(recipient);
        when(chatRepository.findById(chatId)).thenReturn(java.util.Optional.of(chat));

        // When
        messageService.setMessageToSeen(chatId, authentication);

        // Then
        verify(messageRepository, times(1)).setMessagesToSeenByChatId(chatId, MessageState.SEEN);
        verify(notificationService, times(1)).sendNotification(anyString(), any(Notification.class));
    }

    @Test
    void uploadMediaMessage_ShouldSaveMessageAndSendNotification() {
        // Given
        String chatId = "chat123";
        MultipartFile file = mock(MultipartFile.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        User sender = new User();
        sender.setId("user1");
        User recipient = new User();
        recipient.setId("user2");

        Chat chat = mock(Chat.class);
        when(chat.getId()).thenReturn(chatId);
        when(chat.getSender()).thenReturn(sender);
        when(chat.getRecipient()).thenReturn(recipient);
        when(chatRepository.findById(chatId)).thenReturn(java.util.Optional.of(chat));

        when(fileService.saveFile(file, "user1")).thenReturn("/path/to/file");

        // When
        messageService.uploadMediaMessage(chatId, file, authentication);

        // Then
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(notificationService, times(1)).sendNotification(anyString(), any(Notification.class));
    }
} 