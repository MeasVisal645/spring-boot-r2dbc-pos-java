package backend.Entities;

import backend.Utils.DateStringUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("purchaseOrder")
public class PurchaseOrder {

    public static final String LABEL = "purchaseOrder";
    public static final String ID_COLUMN = "id";
    public static final String SUPPLIER_ID_COLUMN = "supplierId";
    public static final String ORDER_DATE_COLUMN = "orderDate";
    public static final String STATUS_COLUMN = "status";
    public static final String CREATED_DATE_COLUMN = "createdDate";
    public static final String UPDATED_DATE_COLUMN = "updatedDate";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(SUPPLIER_ID_COLUMN)
    private Long supplierId;
    @Column(ORDER_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime orderDate;
    @Column(STATUS_COLUMN)
    private Status status;
    @Column(CREATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime createdDate;
    @Column(UPDATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime updatedDate;

    public static PurchaseOrderBuilder from(PurchaseOrder purchaseOrder) {
        return PurchaseOrder.builder()
                .id(purchaseOrder.getId())
                .supplierId(purchaseOrder.getSupplierId())
                .orderDate(purchaseOrder.getOrderDate())
                .status(purchaseOrder.getStatus());
    }

    public static PurchaseOrder update(PurchaseOrder existing, PurchaseOrder updated) {
        existing.setSupplierId(updated.getSupplierId());
        existing.setOrderDate(updated.getOrderDate());
        existing.setStatus(updated.getStatus());
        return existing;
    }
}
