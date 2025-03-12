package com.quangntn.whatsappclone.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_ShouldReturnUserResponses() {
        // Given
        Authentication authentication = mock(Authentication.class);
        List<UserResponse> userResponses = List.of(UserResponse.builder().id("user456").build());
        when(userService.getAllUsersExceptSelf(authentication)).thenReturn(userResponses);

        // When
        ResponseEntity<List<UserResponse>> response = userController.getAllUsers(authentication);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponses, response.getBody());
        verify(userService, times(1)).getAllUsersExceptSelf(authentication);
    }
} 