package backend.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DashboardResponse(
        Long totalCustomers,
        BigDecimal totalSales,
        Long totalOrders,
        Long totalProducts
) {
}
