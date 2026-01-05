package com.example.demo.controllers;

import com.example.demo.TestUtil;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private Authentication authentication = mock(Authentication.class);

    @BeforeEach
    public void setUp() {
        cartController = new CartController();
        TestUtil.injectObject(cartController, "userRepository", userRepository);
        TestUtil.injectObject(cartController, "cartRepository", cartRepository);
        TestUtil.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart_happy_path() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setCart(new Cart());

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        when(authentication.getName()).thenReturn("testUser");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(request, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart responseCart = response.getBody();
        assertNotNull(responseCart);
        assertEquals(2, responseCart.getItems().size());
    }

    @Test
    public void add_to_cart_unauthorized() {
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        when(authentication.getName()).thenReturn("hacker");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.addTocart(request, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void add_to_cart_user_not_found() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("unknownUser");
        request.setItemId(1L);
        request.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.addTocart(request, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void add_to_cart_item_not_found() {
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        when(authentication.getName()).thenReturn("testUser");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(99L);
        request.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.addTocart(request, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_from_cart_happy_path() {
        User user = new User();
        user.setUsername("testUser");
        Cart cart = new Cart();
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(1.00));
        cart.addItem(item);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        when(authentication.getName()).thenReturn("testUser");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(request, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getItems().size());
    }

    @Test
    public void remove_from_cart_unauthorized() {
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        when(authentication.getName()).thenReturn("otherUser");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(request, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}