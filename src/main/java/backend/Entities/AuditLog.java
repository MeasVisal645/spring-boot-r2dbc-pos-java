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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("auditLog")
public class AuditLog {

    public static final String LABEL = "auditLog";
    public static final String ID_COLUMN = "id";
    public static final String USER_ID_COLUMN = "userId";
    public static final String ACTION_COLUMN = "action";
    public static final String IP_ADDRESS_COLUMN = "ipAddress";
    public static final String USER_AGENT_COLUMN = "userAgent";
    public static final String TIMESTAMP_COLUMN = "timestamp";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(USER_ID_COLUMN)
    private Long userId;
    @Column(ACTION_COLUMN)
    private String action;
    @Column(IP_ADDRESS_COLUMN)
    private String ipAddress;
    @Column(USER_AGENT_COLUMN)
    private String userAgent;
    @Column(TIMESTAMP_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime timestamp;
}
