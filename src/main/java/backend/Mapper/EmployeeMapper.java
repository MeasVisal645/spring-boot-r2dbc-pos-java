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
        dto.setActive(employee.isActive());
        return dto;
    }

    // Convert from DTO to Entity
    public static Employee toEntity(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }

        Employee entity = new Employee();
        entity.setId(entity.getId());
        entity.setFirstName(entity.getFirstName());
        entity.setLastName(entity.getLastName());
        entity.setEmail(entity.getEmail());
//        entity.setActive(entity.isActive());

        return entity;
    }
}
