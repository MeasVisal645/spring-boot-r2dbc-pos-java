package backend.Dto;

import backend.Entities.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderRequest(

        Long supplierId,
        Status status,
        LocalDateTime orderDate,
        List<PurchaseItemRequest> items

) {
}
