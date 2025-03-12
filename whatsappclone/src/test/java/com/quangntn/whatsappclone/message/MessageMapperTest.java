package com.quangntn.whatsappclone.message;

import com.quangntn.whatsappclone.file.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class MessageMapperTest {

    private MessageMapper messageMapper;

    @BeforeEach
    void setUp() {
        messageMapper = new MessageMapper();
    }

    @Test
    void toMessageResponse_ShouldMapFieldsCorrectly() {
        // Given
        Message message = new Message();
        message.setId(123L);
        message.setContent("Hello World");
        message.setSenderId("user1");
        message.setRecipientId("user2");
        message.setType(MessageType.TEXT);
        message.setState(MessageState.SENT);
        message.setCreatedDate(LocalDateTime.now());
        message.setMediaFilePath("/path/to/media");

        byte[] mediaContent = new byte[]{1, 2, 3};

        try (MockedStatic<FileUtils> mockedFileUtils = mockStatic(FileUtils.class)) {
            mockedFileUtils.when(() -> FileUtils.readFileFromLocation("/path/to/media")).thenReturn(mediaContent);

            // When
            MessageResponse response = messageMapper.toMessageResponse(message);

            // Then
            assertNotNull(response);
            assertEquals(message.getId(), response.getId());
            assertEquals(message.getContent(), response.getContent());
            assertEquals(message.getSenderId(), response.getSenderId());
            assertEquals(message.getRecipientId(), response.getRecipientId());
            assertEquals(message.getType(), response.getType());
            assertEquals(message.getState(), response.getState());
            assertEquals(message.getCreatedDate(), response.getCreatedAt());
            assertArrayEquals(mediaContent, response.getMedia());
        }
    }
} 