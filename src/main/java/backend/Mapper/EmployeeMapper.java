package backend.Mapper;

import backend.Dto.EmployeeDto;
import backend.Entities.Employee;


public class EmployeeMapper {

    // Convert from Entity to Dto
    public static EmployeeDto toDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setNatId(employee.getNatId());
        dto.setNssfId(employee.getNssfId());
        dto.setImageUrl(employee.getImageUrl());
        dto.setActive(employee.isActive());
        return dto;
    }

    // Convert from DTO to Entity
    public static Employee toEntity(EmployeeDto dto) {
        if (dto == null) return null;

        Employee entity = new Employee();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setNatId(dto.getNatId());
        entity.setNssfId(dto.getNssfId());
        entity.setActive(dto.isActive());
        entity.setImageUrl(dto.getImageUrl());

        return entity;
    }


}
