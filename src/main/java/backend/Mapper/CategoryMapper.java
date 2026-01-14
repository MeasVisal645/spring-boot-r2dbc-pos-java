package backend.Mapper;

import backend.Dto.CategoryDto;
import backend.Entities.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setCode(category.getCode());
        dto.setName(category.getName());
        dto.setActive(category.isActive());

        return dto;
    }

    // Convert from DTO to Entity
    public static Category toEntity(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        Category entity = new Category();
        entity.setId(entity.getId());
        entity.setCode(entity.getCode());
        entity.setName(entity.getName());
        entity.setActive(entity.isActive());

        return entity;
    }
}
