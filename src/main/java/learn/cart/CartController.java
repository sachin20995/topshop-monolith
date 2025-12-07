package learn.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepo;

    // Get cart for a user
    @GetMapping("/{phoneNumber}")
    public List<CartItem> getCart(@PathVariable Long phoneNumber) {
        Cart cart = cartRepo.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setPhoneNumber(phoneNumber);
                    return cartRepo.save(newCart);
                });
        return cart.getItems();
    }

    // Add product to cart
    @PostMapping("/{phoneNumber}/add")
    public Cart addToCart(@PathVariable long phoneNumber, @RequestBody CartItem item) {
    	Cart cart = cartRepo.findByPhoneNumber(phoneNumber).orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setPhoneNumber(phoneNumber);
            cart = cartRepo.save(cart); // ✅ flush and get managed entity
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(ci -> ci.getFilename().equals(item.getFilename()))
            .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(item.getQuantity());
        } else {
            item.setCart(cart);
            cart.getItems().add(item);
        }

        return cartRepo.save(cart);
    }

    // Remove product
    @DeleteMapping("/{phoneNumber}/remove/{filename}")
    public Cart removeFromCart(@PathVariable long phoneNumber, @PathVariable String filename) {
        Cart cart = cartRepo.findByPhoneNumber(phoneNumber).orElseThrow();
        cart.getItems().removeIf(item -> item.getFilename().toString().equals(filename));
        return cartRepo.save(cart);
    }

    // Clear cart
    @DeleteMapping("/{phoneNumber}/clear")
    public void clearCart(@PathVariable Integer phoneNumber) {
        cartRepo.findByPhoneNumber(phoneNumber).ifPresent(cartRepo::delete);
    }
}
