package com.evlarus.ecomreturns.cart;

import com.evlarus.ecomreturns.cart.domain.Cart;
import com.evlarus.ecomreturns.cart.domain.CartItem;
import com.evlarus.ecomreturns.cart.infrastructure.CartRepository;
import com.evlarus.ecomreturns.catalog.domain.Product;
import com.evlarus.ecomreturns.catalog.infrastructure.ProductRepository;
import com.evlarus.ecomreturns.common.exception.BusinessRuleViolationException;
import com.evlarus.ecomreturns.common.exception.ResourceNotFoundException;
import com.evlarus.ecomreturns.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public Cart addItem(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Товар", productId));

        if (product.getStockQuantity() < quantity) {
            throw new BusinessRuleViolationException("Недостаточно товара на складе: " + product.getName());
        }

        Cart cart = getOrCreateCart(user);
        CartItem existing = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPriceSnapshot(product.getPrice());
            cart.getItems().add(item);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItemQuantity(User user, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(user);
        CartItem item = findItem(cart, itemId);
        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(User user, Long itemId) {
        Cart cart = getOrCreateCart(user);
        CartItem item = findItem(cart, itemId);
        cart.getItems().remove(item);
        return cartRepository.save(cart);
    }

    private CartItem findItem(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Позиция корзины", itemId));
    }
}
