package learn.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
	ProductImage findByfilename(String filename);
}
