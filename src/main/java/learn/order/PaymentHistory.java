package learn.order;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistory {

    private String orderId;
    private String paymentId;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String phoneNumber;
    private String date;
    private String status;
    @OneToMany(mappedBy = "paymentHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderProduct> items = new ArrayList<>();
}
