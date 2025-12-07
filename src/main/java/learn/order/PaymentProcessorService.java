package learn.order;

import java.util.List;

import org.springframework.stereotype.Service;

import learn.cart.CartService;
import learn.product.InventoryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentProcessorService {

    private final OrderHistoryRepository orderHistoryRepo;
    private final CartService cartService;
    private final InventoryService inventoryService;

    public void processOrderSuccess(String phoneNumber, List<OrderItem> items) {

        // 1️⃣ SAVE ORDER
        PaymentHistory history = new PaymentHistory();
        history.setPhoneNumber(phoneNumber);
        history.setStatus("SUCCESS");

        List<OrderProduct> products = items.stream().map(i -> {
            OrderProduct p = new OrderProduct();
            p.setFilename(i.getFilename());
            p.setPrice(i.getPrice());
            p.setQuantity(i.getQuantity());
            p.setPaymentHistory(history);
            return p;
        }).toList();

        history.setItems(products);
        orderHistoryRepo.save(history);

        // 2️⃣ CLEAR CART
        cartService.clearCart(phoneNumber);

        // 3️⃣ UPDATE INVENTORY
        inventoryService.updateInventory(items);
    }


    public void processOrderSuccess(String phoneNumber,
                                    List<OrderItem> items,
                                    String orderId,
                                    String paymentId,
                                    String date) {

        // 1️⃣ SAVE ORDER
        PaymentHistory history = new PaymentHistory();
        history.setPhoneNumber(phoneNumber);
        history.setOrderId(orderId);
        history.setPaymentId(paymentId);
        history.setDate(date);
        history.setStatus("SUCCESS");

        List<OrderProduct> products = items.stream().map(i -> {
            OrderProduct p = new OrderProduct();
            p.setFilename(i.getFilename());
            p.setPrice(i.getPrice());
            p.setQuantity(i.getQuantity());
            p.setPaymentHistory(history);
            return p;
        }).toList();

        history.setItems(products);
        orderHistoryRepo.save(history);

        // 2️⃣ CLEAR CART
        cartService.clearCart(phoneNumber);

        // 3️⃣ UPDATE INVENTORY
        inventoryService.updateInventory(items);
    }
}