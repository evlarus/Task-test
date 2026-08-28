package com.evlarus.ecomreturns.cart.api;

import com.evlarus.ecomreturns.cart.CartService;
import com.evlarus.ecomreturns.user.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    public CartController(CartService cartService, CurrentUserService currentUserService) {
        this.cartService = cartService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public CartResponse get(Authentication authentication) {
        var user = currentUserService.resolve(authentication);
        return CartResponse.from(cartService.getOrCreateCart(user));
    }

    @PostMapping("/items")
    public CartResponse addItem(@Valid @RequestBody AddCartItemRequest request, Authentication authentication) {
        var user = currentUserService.resolve(authentication);
        return CartResponse.from(cartService.addItem(user, request.productId(), request.quantity()));
    }

    @PatchMapping("/items/{itemId}")
    public CartResponse updateItem(@PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request,
                                    Authentication authentication) {
        var user = currentUserService.resolve(authentication);
        return CartResponse.from(cartService.updateItemQuantity(user, itemId, request.quantity()));
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(@PathVariable Long itemId, Authentication authentication) {
        var user = currentUserService.resolve(authentication);
        return CartResponse.from(cartService.removeItem(user, itemId));
    }
}
