package backend.Dto;

import backend.Utils.DateStringUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesData(
        BigDecimal totalSale,
        @JsonSerialize(using = DateStringUtils.class)
        LocalDateTime saleDate
) {
}
