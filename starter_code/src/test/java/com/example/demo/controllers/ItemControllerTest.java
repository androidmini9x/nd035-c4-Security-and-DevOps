package com.example.demo.controllers;

import com.example.demo.TestUtil;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @BeforeEach
    public void setUp() {
        itemController = new ItemController();
        TestUtil.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_all_items_happy_path() {
        // Setup items
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Round Widget");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Square Widget");

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        final ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void get_item_by_id_happy_path() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Round Widget", response.getBody().getName());
    }

    @Test
    public void get_item_by_id_not_found() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        final ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        // ResponseEntity.of() returns 404 if the Optional is empty
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void get_items_by_name_happy_path() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");

        when(itemRepository.findByName("Round Widget")).thenReturn(Arrays.asList(item));

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Round Widget", response.getBody().get(0).getName());
    }

    @Test
    public void get_items_by_name_not_found() {
        when(itemRepository.findByName("Unknown")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Unknown");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        when(itemRepository.findByName("Unknown")).thenReturn(Collections.emptyList());

        response = itemController.getItemsByName("Unknown");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}