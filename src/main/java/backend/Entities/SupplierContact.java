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
@Table("supplierContact")
public class SupplierContact {

    public static final String LABEL = "supplierContact";
    public static final String ID_COLUMN = "id";
    public static final String SUPPLIER_ID_COLUMN = "supplierId";
    public static final String CONTACT_NAME_COLUMN = "name";
    public static final String EMAIL_COLUMN = "email";
    public static final String PHONE_COLUMN = "phone";
    public static final String POSITION_COLUMN = "position";
    public static final String IS_ACTIVE_COLUMN = "isActive";
    public static final String CREATED_DATE_COLUMN = "createdDate";
    public static final String UPDATED_DATE_COLUMN = "updatedDate";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(SUPPLIER_ID_COLUMN)
    private Long supplierId;
    @Column(CONTACT_NAME_COLUMN)
    private String name;
    @Column(EMAIL_COLUMN)
    private String email;
    @Column(PHONE_COLUMN)
    private String phone;
    @Column(POSITION_COLUMN)
    private String position;
    @Column(IS_ACTIVE_COLUMN)
    private boolean isActive;
    @Column(CREATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime createdDate;
    @Column(UPDATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime updatedDate;

    public static SupplierContactBuilder from(SupplierContact supplierContact) {
        return SupplierContact.builder()
                .id(supplierContact.getId())
                .supplierId(supplierContact.getSupplierId())
                .name(supplierContact.getName())
                .email(supplierContact.getEmail())
                .phone(supplierContact.getPhone())
                .position(supplierContact.getPosition())
                .isActive(supplierContact.isActive())
                .createdDate(supplierContact.getCreatedDate())
                .updatedDate(supplierContact.getUpdatedDate());
    }
}
