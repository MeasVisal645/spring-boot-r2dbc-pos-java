package backend.Mapper;

import backend.Dto.CustomerDto;
import backend.Entities.Customer;
import backend.Entities.Expense;
import backend.Entities.ExpenseDto;

public class ExpenseMapper {

    // Convert from Entity to Dto
    public static ExpenseDto toDto(Expense entity) {
        if (entity == null) {
            return null;
        }

        ExpenseDto dto = new ExpenseDto();
        dto.setId(entity.getId());
        dto.setReference(entity.getReference());
        dto.setCategory(entity.getCategory());
        dto.setNote(entity.getNote());
        dto.setAmount(entity.getAmount());
        dto.setUserId(entity.getUserId());
        dto.setCreatedDate(entity.getCreatedDate());

        return dto;
    }

    // Convert from DTO to Entity
    public static Expense toEntity(ExpenseDto dto) {
        if (dto == null) {
            return null;
        }

        Expense entity = new Expense();
        entity.setId(entity.getId());
        entity.setReference(entity.getReference());
        entity.setCategory(entity.getCategory());
        entity.setNote(entity.getNote());
        entity.setAmount(entity.getAmount());
        entity.setUserId(entity.getUserId());


        return entity;
    }
}
