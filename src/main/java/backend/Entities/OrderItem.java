package backend.Entities;

import backend.Utils.DateStringUtils;
import backend.Utils.OrderNoGenerator;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("orderItem")
public class OrderItem {

    public static final String LABEL = "orderItem";
    public static final String ID_COLUMN = "id";
    public static final String ORDER_NO_COLUMN = "orderNo";
    public static final String USER_ID_COLUMN = "userId";
    public static final String CREATED_DATE_COLUMN = "createdDate";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(ORDER_NO_COLUMN)
    private String orderNo;
    @Column(USER_ID_COLUMN)
    private Long userId;
    @Column(CREATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime createdDate;

}
