package learn.product;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage implements Serializable {

	@Id
    private String filename;
    private int quantity;

    @Lob // large object → stores big strings
    @Column(columnDefinition = "LONGTEXT") // MySQL specific
    private String base64Data;
    private long price;
}