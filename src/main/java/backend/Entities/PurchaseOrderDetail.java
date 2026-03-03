package backend.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("purchaseOrderDetail")
public class PurchaseOrderDetail {

    public static final String LABEL = "purchaseOrderDetail";
    public static final String ID_COLUMN = "id";
    public static final String PURCHASE_ID_COLUMN = "purchaseId";
    public static final String PRODUCT_ID_COLUMN = "productId";
    public static final String QUANTITY_COLUMN = "quantity";
    public static final String UNIT_PRICE_COLUMN = "unitPrice";
    public static final String TOTAL_PRICE_COLUMN = "totalPrice";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(PURCHASE_ID_COLUMN)
    private Long purchaseId;
    @Column(PRODUCT_ID_COLUMN)
    private Long productId;
    @Column(QUANTITY_COLUMN)
    private int quantity;
    @Column(UNIT_PRICE_COLUMN)
    private BigDecimal unitPrice;
    @Column(TOTAL_PRICE_COLUMN)
    private BigDecimal totalPrice;

    public static PurchaseOrderDetailBuilder from(PurchaseOrderDetail purchaseOrderDetail) {
        return PurchaseOrderDetail.builder()
                .id(purchaseOrderDetail.getId())
                .purchaseId(purchaseOrderDetail.getPurchaseId())
                .productId(purchaseOrderDetail.getProductId())
                .quantity(purchaseOrderDetail.getQuantity())
                .unitPrice(purchaseOrderDetail.getUnitPrice())
                .totalPrice(purchaseOrderDetail.getTotalPrice());
    }

    public static PurchaseOrderDetail update(PurchaseOrderDetail existing, PurchaseOrderDetail updated) {
        existing.setProductId(updated.getProductId());
        existing.setQuantity(updated.getQuantity());
        existing.setUnitPrice(updated.getUnitPrice());
        existing.setTotalPrice(updated.getTotalPrice());
        return existing;
    }
}
