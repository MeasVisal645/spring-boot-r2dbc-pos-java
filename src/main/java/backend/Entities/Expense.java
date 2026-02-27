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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("expense")
public class Expense {

    public static final String LABEL = "expense";
    public static final String ID_COLUMN = "id";
    public static final String REFERENCE_COLUMN = "reference";
    public static final String CATEGORY_COLUMN = "category";
    public static final String NOTE_COLUMN = "note";
    public static final String AMOUNT_COLUMN = "amount";
    public static final String USER_ID_COLUMN = "userId";
    public static final String IS_COMPLETE_COLUMN = "isComplete";
    public static final String CREATED_DATE_COLUMN = "createdDate";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(REFERENCE_COLUMN)
    private String reference;
    @Column(CATEGORY_COLUMN)
    private String category;
    @Column(NOTE_COLUMN)
    private String note;
    @Column(AMOUNT_COLUMN)
    private BigDecimal amount;
    @Column(USER_ID_COLUMN)
    private Long userId;
    @Column(IS_COMPLETE_COLUMN)
    private boolean isComplete;
    @Column(CREATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime createdDate;

    public static ExpenseBuilder from(Expense expense) {
        return Expense.builder()
                .id(expense.getId())
                .reference(expense.getReference())
                .category(expense.getCategory())
                .note(expense.getNote())
                .amount(expense.getAmount())
                .isComplete(expense.isComplete())
                .userId(expense.getUserId());
    }

    public static Expense update(Expense existing, Expense updated) {
        existing.setReference(updated.getReference());
        existing.setCategory(updated.getCategory());
        existing.setNote(updated.getNote());
        existing.setAmount(updated.getAmount());
        existing.setComplete(updated.isComplete());
        return existing;
    }
}
