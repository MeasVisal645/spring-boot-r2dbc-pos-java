package backend.Dto;

import backend.Entities.Category;
import backend.Entities.Product;
import backend.Entities.Unit;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

public record CategoryProduct(
        Category category,
        List<Product> products
) {
}
