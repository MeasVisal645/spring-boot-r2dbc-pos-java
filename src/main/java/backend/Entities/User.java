package backend.Entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("user")
public class User {
    public final static String LABEL = "USER";
    public final static String ID_COLUMN = "id";
    public final static String USERNAME_COLUMN = "username";
    public final static String PASSWORD_COLUMN = "password";
    public final static String EMAIL_COLUMN = "email";
    public final static String USER_ROLE = "role";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(USERNAME_COLUMN)
    private String username;
    @Column(PASSWORD_COLUMN)
    private String password;
    @Column(EMAIL_COLUMN)
    private String email;
    @Column(USER_ROLE)
    private UserRole userRole;

}
