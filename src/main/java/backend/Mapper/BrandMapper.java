package backend.Mapper;

import backend.Dto.BrandDto;
import backend.Dto.SupplierDto;
import backend.Entities.Brand;
import backend.Entities.Supplier;

public class BrandMapper {
    // Convert from Entity to DTO
    public static BrandDto toDto(Brand brand) {
        if (brand == null) {
            return null;
        }

        BrandDto dto = new BrandDto();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setActive(brand.isActive());

        return dto;
    }

    // Convert from DTO to Entity
    public static Brand toEntity(BrandDto dto) {
        if (dto == null) {
            return null;
        }

        Brand entity = new Brand();
        entity.setId(entity.getId());
        entity.setName(entity.getName());
        entity.setActive(entity.isActive());

        return entity;
    }
}
