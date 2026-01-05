package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username, Authentication authentication) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
            log.error("EVENT=FinishOrder STATUS=FAILURE error={}", "User not found");
            return ResponseEntity.notFound().build();
		}

        if (!user.getUsername().equals(authentication.getName())) {
            log.error("EVENT=FinishOrder STATUS=UNAUTHORIZED userId={} error={}", user.getId(), "Unauthorized access attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);

        log.info("EVENT=FinishOrder STATUS=SUCCESS userId={} orderId={}", user.getId(), order.getId());

        return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username, Authentication authentication) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			return ResponseEntity.notFound().build();
		}

        if (!user.getUsername().equals(authentication.getName())) {
            log.error("EVENT=GetOrder STATUS=UNAUTHORIZED userId={} error={}", user.getId(), "Unauthorized access attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
