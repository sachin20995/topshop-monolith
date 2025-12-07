package learn.product;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageRepository imageRepo;

    @PostMapping("/images")
    public ResponseEntity<ProductImage> uploadImage(@RequestBody ProductImage image) {
        ProductImage saved = imageRepo.save(image);
        return ResponseEntity.ok(saved);
    }

    @Cacheable("products")
    @GetMapping("/images")
    public List<ProductImage> getAllImages() {
    	System.out.println("Fetching from DB...");
        return imageRepo.findAll();
    }
    
    @CacheEvict(value = "products", allEntries = true) // ✅ Clear cache when products change
    public ProductImage saveProduct(ProductImage product) {
        return imageRepo.save(product);
    }
}