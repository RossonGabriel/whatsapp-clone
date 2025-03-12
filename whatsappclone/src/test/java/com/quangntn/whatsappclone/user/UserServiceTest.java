package com.quangntn.whatsappclone.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsersExceptSelf_ShouldReturnUserResponses() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user123");

        User user = new User();
        user.setId("user456");
        List<User> users = List.of(user);

        when(userRepository.findAllUsersExceptSelf("user123")).thenReturn(users);

        UserResponse userResponse = UserResponse.builder().id("user456").build();
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        // When
        List<UserResponse> result = userService.getAllUsersExceptSelf(authentication);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user456", result.get(0).getId());

        verify(userRepository, times(1)).findAllUsersExceptSelf("user123");
        verify(userMapper, times(1)).toUserResponse(user);
    }
} 