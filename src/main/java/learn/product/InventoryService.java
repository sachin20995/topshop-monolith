package learn.product;
import java.util.List;

import org.springframework.stereotype.Service;

import learn.order.OrderItem;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductImageRepository productRepo;

    public void updateInventory(List<OrderItem> items) {

        for (OrderItem item : items) {
            ProductImage product = productRepo.findByfilename(item.getFilename());

            if (product == null) continue;

            int newQty = product.getQuantity() - item.getQuantity();
            product.setQuantity(Math.max(newQty, 0));

            productRepo.save(product);

            System.out.println("📦 Updated inventory for product " 
                + product.getFilename() + " → qty = " + newQty);
        }
    }
}