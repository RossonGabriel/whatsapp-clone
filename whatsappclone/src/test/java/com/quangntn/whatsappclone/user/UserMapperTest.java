package com.quangntn.whatsappclone.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void fromTokenAttributes_WithAllAttributes_ShouldMapCorrectly() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123");
        attributes.put("given_name", "John");
        attributes.put("family_name", "Doe");
        attributes.put("email", "john.doe@example.com");

        // When
        User user = userMapper.fromTokenAttributes(attributes);

        // Then
        assertNotNull(user);
        assertEquals("123", user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertNotNull(user.getLastSeen());
    }

    @Test
    void fromTokenAttributes_WithNicknameNoGivenName_ShouldUseNickname() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123");
        attributes.put("nickname", "Johnny");
        attributes.put("email", "john@example.com");

        // When
        User user = userMapper.fromTokenAttributes(attributes);

        // Then
        assertNotNull(user);
        assertEquals("Johnny", user.getFirstName());
    }

    @Test
    void fromTokenAttributes_WithMissingAttributes_ShouldHandleNullsGracefully() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123");

        // When
        User user = userMapper.fromTokenAttributes(attributes);

        // Then
        assertNotNull(user);
        assertEquals("123", user.getId());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getEmail());
        assertNotNull(user.getLastSeen());
    }

    @Test
    void toUserResponse_ShouldMapAllFields() {
        // Given
        User user = new User();
        user.setId("123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setLastSeen(LocalDateTime.now());

        // When
        UserResponse response = userMapper.toUserResponse(user);

        // Then
        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getLastName(), response.getLastName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getLastSeen(), response.getLastSeen());
        assertEquals(user.isUserOnline(), response.isOnline());
    }
} 