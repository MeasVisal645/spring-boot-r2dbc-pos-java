package backend.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("payment")
public class Payment {

    public static final String LABEL = "payment";
    public static final String ID_COLUMN = "id";
    public static final String PAYMENT_METHOD_COLUMN = "paymentMethod";

    private Long id;
    private PaymentMethod paymentMethod;


}
