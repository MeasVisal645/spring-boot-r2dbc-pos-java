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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("orderDetail")
public class OrderDetail {

    public static final String LABEL = "orderDetail";
    public static final String ID_COLUMN = "id";
    public static final String ORDER_ID_COLUMN = "orderId";
    public static final String PRODUCT_ID_COLUMN = "productId";
    public static final String QUANTITY_COLUMN = "quantity";
    public static final String TOTAL_COLUMN = "total";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(ORDER_ID_COLUMN)
    private Long orderId;
    @Column(PRODUCT_ID_COLUMN)
    private Long productId;
    @Column(QUANTITY_COLUMN)
    private int quantity;
    @Column(TOTAL_COLUMN)
    private BigDecimal total;

}
