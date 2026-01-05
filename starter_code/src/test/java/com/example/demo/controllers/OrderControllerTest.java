package com.example.demo.controllers;

import com.example.demo.TestUtil;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private Authentication authentication = mock(Authentication.class);

    @BeforeEach
    public void setUp() {
        orderController = new OrderController();
        TestUtil.injectObject(orderController, "userRepository", userRepository);
        TestUtil.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_order_happy_path() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Item item = new Item();
        item.setPrice(BigDecimal.valueOf(9.99));

        Cart cart = new Cart();
        cart.addItem(item);
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        when(authentication.getName()).thenReturn("testUser");

        final ResponseEntity<UserOrder> response = orderController.submit("testUser", authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
    }

    @Test
    public void submit_order_unauthorized() {
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        when(authentication.getName()).thenReturn("hacker");

        final ResponseEntity<UserOrder> response = orderController.submit("testUser", authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void submit_order_user_not_found() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit("unknown", authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void get_orders_for_user_happy_path() {
        User user = new User();
        user.setUsername("testUser");
        user.setId(1L);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(authentication.getName()).thenReturn("testUser");

        List<UserOrder> orders = new ArrayList<>();
        UserOrder order = new UserOrder();
        orders.add(order);

        when(orderRepository.findByUser(user)).thenReturn(orders);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser", authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void get_orders_for_user_unauthorized() {
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        // Mismatch
        when(authentication.getName()).thenReturn("otherUser");

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser", authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void get_orders_for_user_not_found() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("unknown", authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}