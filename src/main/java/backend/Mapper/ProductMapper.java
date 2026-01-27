package backend.Mapper;

import backend.Dto.ProductDto;
import backend.Entities.Product;

public class ProductMapper {
    // Convert from Entity to DTO
    public static ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setUnit(product.getUnit());
        dto.setActive(product.isActive());

        return dto;
    }

    // Convert from DTO to Entity
    public static Product toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        Product entity = new Product();
        entity.setId(entity.getId());
        entity.setCode(entity.getCode());
        entity.setName(entity.getName());
        entity.setPrice(entity.getPrice());
        entity.setQuantity(entity.getQuantity());
        entity.setActive(entity.isActive());

        return entity;
    }
}
