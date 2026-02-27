package backend.Mapper;

import backend.Dto.ProductDto;
import backend.Dto.SupplierDto;
import backend.Entities.Product;
import backend.Entities.Supplier;

public class SupplierMapper {
    // Convert from Entity to DTO
    public static SupplierDto toDto(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierDto dto = new SupplierDto();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());

        return dto;
    }

    // Convert from DTO to Entity
    public static Supplier toEntity(SupplierDto dto) {
        if (dto == null) {
            return null;
        }

        Supplier entity = new Supplier();
        entity.setId(entity.getId());
        entity.setName(entity.getName());

        return entity;
    }
}
