package backend.Dto;

import backend.Entities.Product;
import backend.Entities.Unit;
import backend.Utils.DateStringUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private Long id;
    private String code;
    private String name;
    private BigDecimal price;
    private int quantity;
    private Unit unit;
    private boolean isActive;
    private String imageUrl;
    private Long categoryId;

}
