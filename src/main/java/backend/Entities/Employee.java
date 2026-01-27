package backend.Entities;

import backend.Dto.EmployeeDto;
import backend.Utils.DateStringUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("employee")
public class Employee {

    public final static String ID_COLUMN = "id";
    public final static String FIRST_NAME_COLUMN = "firstName";
    public final static String LAST_NAME_COLUMN = "lastName";
    public final static String EMAIL_COLUMN = "email";
    public final static String BRANCH_ID_COLUMN = "branchId";
    public final static String IS_ACTIVE_COLUMN = "isActive";
    public static final String CREATED_DATE_COLUMN = "createdDate";
    public static final String UPDATED_DATE_COLUMN = "updatedDate";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(FIRST_NAME_COLUMN)
    private String firstName;
    @Column(LAST_NAME_COLUMN)
    private String lastName;
    @Column(EMAIL_COLUMN)
    private String email;
    @Column(IS_ACTIVE_COLUMN)
    private boolean isActive;
    @Column(CREATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime createdDate;
    @Column(UPDATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime updatedDate;

    public static EmployeeBuilder from(Employee employee) {
        return Employee.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .isActive(employee.isActive())
                .createdDate(employee.getCreatedDate())
                .updatedDate(employee.getUpdatedDate());
    }

    public static Employee update(Employee existingEmployee, Employee updatedEmployee) {
        existingEmployee.setFirstName(updatedEmployee.getFirstName());
        existingEmployee.setLastName(updatedEmployee.getLastName());
        existingEmployee.setEmail(updatedEmployee.getEmail());
        existingEmployee.setActive(updatedEmployee.isActive());
        return existingEmployee;
    }

}
