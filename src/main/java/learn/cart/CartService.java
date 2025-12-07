package learn.cart;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public void clearCart(String phoneNumber) {

        Long id = Long.parseLong(phoneNumber);

        cartRepository.deleteById(id);

        System.out.println("🛒 Cart cleared for user " + phoneNumber);
    }
}