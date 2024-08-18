package com.library.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.library.enums.Gender;
import com.library.enums.UserType;
import com.library.model.entities.User;
import com.library.model.request.CreateUserDto;
import com.library.model.request.SignInDto;
import com.library.model.request.updateUserDto;
import com.library.model.response.AppResponse;
import com.library.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addUser_ShouldReturnSuccess_WhenUserIsValid() {
        // Arrange
        CreateUserDto userDto = new CreateUserDto();
        userDto.setUsername("johndoe");
        userDto.setPassword("securepassword123");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setGender(Gender.MALE);
        userDto.setType(UserType.ADMIN);

        User user = User.builder()
                .username("johndoe")
                .password("securepassword123") // Ensure this matches any hashing done in actual service
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .type(UserType.ADMIN)
                .build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        AppResponse response = userService.addUser(userDto);

        // Assert
        assertEquals("User added successfully", response.getMessage());
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof User);
        User addedUser = (User) response.getData();
        assertEquals("johndoe", addedUser.getUsername());
    }

    @Test
    void updateUser_ShouldReturnSuccess_WhenUserExists() {
        // Arrange
        updateUserDto userDto = new updateUserDto();
        userDto.setUsername("johndoe");
        userDto.setFirstName("Jane");

        User existingUser = new User();
        existingUser.setUsername("johndoe");
        existingUser.setFirstName("John");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        AppResponse response = userService.updateUser(userDto);

        // Assert
        assertEquals("User updated successfully", response.getMessage());
        assertEquals("00", response.getCode());
        assertNotNull(response.getData());
        User updatedUser = (User) response.getData();
        assertEquals("Jane", updatedUser.getFirstName());
    }

    @Test
    void deleteUser_ShouldReturnSuccess_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        AppResponse response = userService.deleteUser(userId);

        // Assert
        assertEquals("User deleted successfully", response.getMessage());
        assertEquals("00", response.getCode());
        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        AppResponse response = userService.getUserById(userId);

        // Assert
        assertEquals("User detail retrieved successfully", response.getMessage());
        assertEquals("00", response.getCode());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof User);
    }

    @Test
    void signIn_ShouldReturnSuccess_WhenCredentialsAreValid() {
        // Arrange
        SignInDto signInDto = new SignInDto();
        signInDto.setUsername("johndoe");
        signInDto.setPassword("securepassword123");

        User existingUser = User.builder()
                .username("johndoe")
                .password("securepassword123") // Ensure this matches any hashing done in actual service
                .build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));

        // Act
        AppResponse response = userService.signIn(signInDto);

        // Assert
        assertEquals("User logged in successfully", response.getMessage());
        assertEquals("00", response.getCode());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof User);
        User loggedInUser = (User) response.getData();
        assertEquals("johndoe", loggedInUser.getUsername());
    }

    @Test
    void signIn_ShouldReturnError_WhenUserDoesNotExist() {
        // Arrange
        SignInDto signInDto = new SignInDto();
        signInDto.setUsername("johndoe");
        signInDto.setPassword("securepassword123");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        // Act
        AppResponse response = userService.signIn(signInDto);

        // Assert
        assertEquals("User does not exist", response.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getCode());
        assertNull(response.getData());
    }

    @Test
    void signIn_ShouldReturnError_WhenPasswordIsIncorrect() {
        // Arrange
        SignInDto signInDto = new SignInDto();
        signInDto.setUsername("johndoe");
        signInDto.setPassword("wrongpassword");

        User existingUser = User.builder()
                .username("johndoe")
                .password("securepassword123") // Ensure this matches any hashing done in actual service
                .build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));

        // Act
        AppResponse response = userService.signIn(signInDto);

        // Assert
        assertEquals("Wrong password", response.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getCode());
        assertNull(response.getData());
    }
}
