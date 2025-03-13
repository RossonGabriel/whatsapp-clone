package com.quangntn.whatsappclone.chat;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.quangntn.whatsappclone.user.User;
import com.quangntn.whatsappclone.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatMapper chatMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ChatService chatService;

    private static final String USER_ID = "user123";
    private static final String SENDER_ID = "sender123";
    private static final String RECIPIENT_ID = "recipient456";
    private static final String CHAT_ID = "chat789";

    private User sender;
    private User recipient;
    private Chat chat;
    private ChatResponse chatResponse;

    @BeforeEach
    void setUp() {
        // Setup sender
        sender = new User();
        sender.setId(SENDER_ID);
        sender.setFirstName("John");
        sender.setLastName("Doe");

        // Setup recipient
        recipient = new User();
        recipient.setId(RECIPIENT_ID);
        recipient.setFirstName("Jane");
        recipient.setLastName("Smith");

        // Setup chat
        chat = new Chat();
        chat.setId(CHAT_ID);
        chat.setSender(sender);
        chat.setRecipient(recipient);

        // Setup chat response
        chatResponse = ChatResponse.builder()
                .id(CHAT_ID)
                .name("Jane Smith")
                .senderId(SENDER_ID)
                .recipientId(RECIPIENT_ID)
                .build();
    }

    @Test
    void getChatsByRecipientId_ShouldReturnChats() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        when(chatRepository.findChatsBySenderId(USER_ID)).thenReturn(List.of(chat));
        when(chatMapper.toChatResponse(chat, USER_ID)).thenReturn(chatResponse);

        // When
        List<ChatResponse> result = chatService.getChatsByRecipientId(authentication);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CHAT_ID, result.get(0).getId());
        assertEquals(SENDER_ID, result.get(0).getSenderId());
        assertEquals(RECIPIENT_ID, result.get(0).getRecipientId());

        verify(chatRepository).findChatsBySenderId(USER_ID);
        verify(chatMapper).toChatResponse(chat, USER_ID);
    }

    @Test
    void getChatsByRecipientId_WhenNoChats_ShouldReturnEmptyList() {
        // Given
        when(authentication.getName()).thenReturn(USER_ID);
        when(chatRepository.findChatsBySenderId(USER_ID)).thenReturn(List.of());

        // When
        List<ChatResponse> result = chatService.getChatsByRecipientId(authentication);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(chatRepository).findChatsBySenderId(USER_ID);
        verifyNoInteractions(chatMapper);
    }

    @Test
    void createChat_WhenChatDoesNotExist_ShouldCreateNewChat() {
        // Given
        when(chatRepository.findChatByRecipientAndSender(SENDER_ID, RECIPIENT_ID))
                .thenReturn(Optional.empty());
        when(userRepository.findByPublicId(SENDER_ID)).thenReturn(Optional.of(sender));
        when(userRepository.findByPublicId(RECIPIENT_ID)).thenReturn(Optional.of(recipient));
        when(chatRepository.save(any(Chat.class))).thenReturn(chat);

        // When
        String result = chatService.createChat(SENDER_ID, RECIPIENT_ID);

        // Then
        assertEquals(CHAT_ID, result);
        verify(chatRepository).findChatByRecipientAndSender(SENDER_ID, RECIPIENT_ID);
        verify(userRepository).findByPublicId(SENDER_ID);
        verify(userRepository).findByPublicId(RECIPIENT_ID);
        verify(chatRepository).save(any(Chat.class));
    }

    @Test
    void createChat_WhenChatExists_ShouldReturnExistingChatId() {
        // Given
        when(chatRepository.findChatByRecipientAndSender(SENDER_ID, RECIPIENT_ID))
                .thenReturn(Optional.of(chat));

        // When
        String result = chatService.createChat(SENDER_ID, RECIPIENT_ID);

        // Then
        assertEquals(CHAT_ID, result);
        verify(chatRepository).findChatByRecipientAndSender(SENDER_ID, RECIPIENT_ID);
        verifyNoInteractions(userRepository);
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void createChat_WhenSenderNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        when(chatRepository.findChatByRecipientAndSender(SENDER_ID, RECIPIENT_ID))
                .thenReturn(Optional.empty());
        when(userRepository.findByPublicId(SENDER_ID)).thenReturn(Optional.empty());

        // When/Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> chatService.createChat(SENDER_ID, RECIPIENT_ID));

        assertEquals("User with id " + SENDER_ID + " not found", exception.getMessage());
        verify(chatRepository).findChatByRecipientAndSender(SENDER_ID, RECIPIENT_ID);
        verify(userRepository).findByPublicId(SENDER_ID);
        verifyNoMoreInteractions(userRepository);
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void createChat_WhenRecipientNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        when(chatRepository.findChatByRecipientAndSender(SENDER_ID, RECIPIENT_ID))
                .thenReturn(Optional.empty());
        when(userRepository.findByPublicId(SENDER_ID)).thenReturn(Optional.of(sender));
        when(userRepository.findByPublicId(RECIPIENT_ID)).thenReturn(Optional.empty());

        // When/Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> chatService.createChat(SENDER_ID, RECIPIENT_ID));

        assertEquals("User with id " + RECIPIENT_ID + " not found", exception.getMessage());
        verify(chatRepository).findChatByRecipientAndSender(SENDER_ID, RECIPIENT_ID);
        verify(userRepository).findByPublicId(SENDER_ID);
        verify(userRepository).findByPublicId(RECIPIENT_ID);
        verify(chatRepository, never()).save(any(Chat.class));
    }
} 