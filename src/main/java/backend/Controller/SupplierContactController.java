package backend.Controller;

import backend.Entities.SupplierContact;
import backend.Service.SupplierContactService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/supplierContact")
@CrossOrigin("*")
@AllArgsConstructor
public class SupplierContactController {

    private final SupplierContactService supplierContactService;

    @PostMapping("/create")
    Mono<SupplierContact> create(@RequestBody SupplierContact supplierContact) {
        return supplierContactService.create(supplierContact);
    }
}
