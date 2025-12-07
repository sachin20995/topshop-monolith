package learn.order;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private int quantity;
    private int price;
//    private String imageData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_history_id")
	@ToString.Exclude
	@JsonBackReference
	private PaymentHistory paymentHistory;
}
