package backend.Entities;

import backend.Dto.ProductDto;
import backend.Utils.DateStringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("product")
public class Product {

    public static final String LABEL = "product";
    public static final String ID_COLUMN = "id";
    public static final String CODE_COLUMN = "code";
    public static final String NAME_COLUMN = "name";
    public static final String COST_COLUMN = "cost";
    public static final String PRICE_COLUMN = "price";
    public static final String QUANTITY_COLUMN = "quantity";
    public static final String UNIT_COLUMN = "unit";
    public static final String BRAND_ID_COLUMN = "brandId";
    public static final String CATEGORY_ID_COLUMN = "categoryId";
    public static final String IS_ACTIVE_COLUMN = "isActive";
    public static final String CREATED_DATE_COLUMN = "createdDate";
    public static final String UPDATED_DATE_COLUMN = "updatedDate";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(CODE_COLUMN)
    private String code;
    @Column(NAME_COLUMN)
    private String name;
    @Column(COST_COLUMN)
    private BigDecimal cost;
    @Column(PRICE_COLUMN)
    private BigDecimal price;
    @Column(QUANTITY_COLUMN)
    private int quantity;
    @Column(UNIT_COLUMN)
    private Unit unit;
    @Column(CATEGORY_ID_COLUMN)
    private Long categoryId;
    @Column(BRAND_ID_COLUMN)
    private Long brandId;
    @Column(IS_ACTIVE_COLUMN)
    private boolean isActive;
    @Column(CREATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime createdDate;
    @Column(UPDATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime updatedDate;

    public static ProductBuilder from(Product product) {
        return Product.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .cost(product.getCost())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .unit(product.getUnit())
                .categoryId(product.getCategoryId())
                .brandId(product.getBrandId());
    }

    public static Product update(Product existingProduct, Product updatedProduct) {
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setCode(updatedProduct.getCode());
        existingProduct.setCost(updatedProduct.getCost());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setUnit(updatedProduct.getUnit());
        existingProduct.setActive(updatedProduct.isActive());
        existingProduct.setCategoryId(updatedProduct.getCategoryId());
        existingProduct.setBrandId(updatedProduct.getBrandId());
        return existingProduct;
    }
}
