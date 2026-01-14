package backend.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("brand")
public class Brand {

    public static final String LABEL = "brand";
    public static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";

    @Id
    @Column(ID_COLUMN)
    private Long id;
    @Column(NAME_COLUMN)
    private String name;

    public static BrandBuilder from(Brand brand){
        return Brand.builder()
                .id(brand.getId())
                .name(brand.getName());
    }

    public static Brand update(Brand brand) {
        brand.setName(brand.getName());
        return brand;
    }

}
