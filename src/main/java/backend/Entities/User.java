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

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("user")
public class User{
    public final static String LABEL = "USER";
    public final static String ID_COLUMN = "id";
    public final static String USERNAME_COLUMN = "username";
    public final static String PASSWORD_COLUMN = "password";
    public final static String ROLE_COLUMN = "role";
    public final static String EMPLOYEE_ID_COLUMN = "employeeId";
    public final static String IMAGE_ID_COLUMN = "imageId";
    public final static String IS_ACTIVE_COLUMN = "isActive";
    public final static String CREATED_DATE_COLUMN = "createdDate";
    public final static String UPDATED_DATE_COLUMN = "updatedDate";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(USERNAME_COLUMN)
    private String username;
    @Column(PASSWORD_COLUMN)
    private String password;
    @Column(ROLE_COLUMN)
    private Role role;
    @Column(EMPLOYEE_ID_COLUMN)
    private Long employeeId;
//    @Column(IMAGE_ID_COLUMN)
//    private Long imageId;
    @Column(IS_ACTIVE_COLUMN)
    private boolean isActive;
    @Column(CREATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime createdDate;
    @Column(UPDATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime updatedDate;

    public static UserBuilder from(User user) {
        return User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .employeeId(user.getEmployeeId())
                .isActive(user.isActive());
    }

    public static User update(User user) {
        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        user.setRole(user.getRole());
        user.setEmployeeId(user.getEmployeeId());
        user.setActive(user.isActive());
        return user;
    }

}
