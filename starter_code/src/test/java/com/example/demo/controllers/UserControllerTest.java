package com.example.demo.controllers;

import com.example.demo.TestUtil;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private CartRepository cartRepository = Mockito.mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = Mockito.mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        TestUtil.injectObject(userController, "userRepository", userRepository);
        TestUtil.injectObject(userController, "cartRepository", cartRepository);
        TestUtil.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        Mockito.when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();

        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        User u = response.getBody();

        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void create_user_password_too_short() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("123"); // Too short
        r.setConfirmPassword("123");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_user_password_mismatch() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("password123");
        r.setConfirmPassword("password321"); // Mismatch

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void find_user_by_id_happy_path() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test", response.getBody().getUsername());
    }

    @Test
    public void find_user_by_id_not_found() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        final ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void find_user_by_username_happy_path() {
        User user = new User();
        user.setUsername("test");

        Mockito.when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test", response.getBody().getUsername());
    }

    @Test
    public void find_user_by_username_not_found() {
        Mockito.when(userRepository.findByUsername("test")).thenReturn(null);

        final ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}