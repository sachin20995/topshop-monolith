package learn.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Value("${razorpay.keyId}")
    private String razorpayKeyId;

    @Value("${razorpay.keySecret}")
    private String razorpayKeySecret;

    private final OrderHistoryRepository orderHistoryRepo;
    private final ObjectMapper objectMapper;
    private final PaymentProcessorService paymentProcessorService; // ➜ NEW service for internal processing

    /**
     * =============================================================
     *  GET ORDER HISTORY (Monolith: Local DB Call Only)
     * =============================================================
     */
    @GetMapping("/orders/{phoneNumber}")
    public ResponseEntity<List<PaymentHistory>> getOrderHistory(@PathVariable String phoneNumber) {

        List<PaymentHistory> histories = orderHistoryRepo.findByPhoneNumber(phoneNumber);

        if (histories == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(histories);
    }


    /**
     * =============================================================
     *  CREATE ORDER (Razorpay or COD)
     * =============================================================
     */
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> data) throws Exception {

        log.info("Creating order for {}", data);

        double amount = Double.parseDouble(data.get("total").toString());
        String phoneNumber = (String) data.get("phoneNumber");
        String paymentMethod = (String) data.get("paymentMethod");

        List<OrderItem> items = objectMapper.convertValue(
                data.get("items"), new TypeReference<List<OrderItem>>() {}
        );

        Map<String, Object> response = new HashMap<>();

        /**
         * ----------------------------------------
         * RAZORPAY → RETURN PAYMENT DETAILS
         * ----------------------------------------
         */
        if ("RAZORPAY".equalsIgnoreCase(paymentMethod)) {

            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject options = new JSONObject();
            options.put("amount", (int) (amount * 100));
            options.put("currency", "INR");
            options.put("receipt", "txn_" + System.currentTimeMillis());

            Order razorOrder = client.orders.create(options);

            log.info("Razorpay order created: {}", (Object)razorOrder.get("id"));

            response.put("status", "created");
            response.put("razorpayOrderId", razorOrder.get("id"));
            response.put("amount", razorOrder.get("amount"));
            response.put("currency", razorOrder.get("currency"));
            response.put("key", razorpayKeyId);
            response.put("phoneNumber", phoneNumber);

            return ResponseEntity.ok(response);
        }

        /**
         * ----------------------------------------
         * COD → DIRECT PAYMENT SUCCESS
         * ----------------------------------------
         */
        paymentProcessorService.processOrderSuccess(phoneNumber, items);

        response.put("status", "success");
        response.put("message", "Order placed with COD");
        response.put("amount", amount);

        return ResponseEntity.ok(response);
    }


    /**
     * =============================================================
     *  VERIFY RAZORPAY PAYMENT (Internal Save + Local Method)
     * =============================================================
     */
    @PostMapping("/payment/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody PaymentVerifyRequest data) {

        Map<String, Object> response = new HashMap<>();

        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", data.getOrderId());
            attributes.put("razorpay_payment_id", data.getPaymentId());
            attributes.put("razorpay_signature", data.getSignature());

            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);

            if (!isValid) {
                response.put("status", "failed");
                return ResponseEntity.badRequest().body(response);
            }

            // SAVE order + items inside DB (monolith internal)
            paymentProcessorService.processOrderSuccess(
                    data.getPhoneNumber(),
                    data.getItems(),
                    data.getOrderId(),
                    data.getPaymentId(),
                    data.getDate()
            );

            response.put("status", "success");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}