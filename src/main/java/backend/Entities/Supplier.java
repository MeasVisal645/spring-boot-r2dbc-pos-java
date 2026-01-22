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
@Table("supplier")
public class Supplier {

    public static final String LABEL = "supplier";
    public static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";
    public static final String CONTACT_COLUMN = "contact";
    public static final String PHONE_COLUMN = "phone";
    public static final String ADDRESS_COLUMN = "address";
    public static final String IS_ACTIVE_COLUMN = "isActive";
    public static final String CREATED_DATE_COLUMN = "createdDate";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(NAME_COLUMN)
    private String name;
    @Column(CONTACT_COLUMN)
    private String contact;
    @Column(PHONE_COLUMN)
    private String phone;
    @Column(ADDRESS_COLUMN)
    private String address;
    @Column(IS_ACTIVE_COLUMN)
    private boolean isActive;
    @Column(CREATED_DATE_COLUMN)
    @JsonSerialize(using = DateStringUtils.class)
    private LocalDateTime createdDate;

    public static SupplierBuilder from(Supplier supplier) {
        return Supplier.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contact(supplier.getContact())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .isActive(supplier.isActive())
                .createdDate(supplier.getCreatedDate());
    }
}
