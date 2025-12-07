package learn.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<PaymentHistory, String> {
	List<PaymentHistory> findByPhoneNumber(String phoneNumber);
}
