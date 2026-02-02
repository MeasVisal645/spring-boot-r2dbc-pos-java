package backend.Dto;

import backend.Entities.Supplier;
import backend.Entities.SupplierContact;

import java.util.List;

public record SupplierDetails(
        Supplier supplier,
        List<SupplierContact> supplierContacts
) {
}
