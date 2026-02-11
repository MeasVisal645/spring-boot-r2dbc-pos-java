package backend.Controller;

import backend.Dto.EmployeeDto;
import backend.Dto.EmployeeUser;
import backend.Entities.Employee;
import backend.Service.EmployeeService;
import backend.Utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
@CrossOrigin("*")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/all")
    private Flux<EmployeeDto> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("{id}")
    public Mono<Employee> findById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

//    @PostMapping("/create")
//    private Mono<Employee> create(@RequestBody Employee employee) {
//        return employeeService.create(employee);
//    }

    @PutMapping("/update")
    private Mono<Employee> update(@RequestBody Employee employee) {
        return employeeService.update(employee);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return employeeService.delete(id);
    }

    @GetMapping
    public Mono<PageResponse<EmployeeUser>> findPagination(@RequestParam Integer pageNumber, Integer pageSize) {
        return employeeService.findPagination(pageNumber, pageSize);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<EmployeeDto> createWithImage(
            @RequestPart("data") Mono<EmployeeDto> data,
            @RequestPart(value = "image", required = false) Mono<FilePart> image
    ) {
        return data.flatMap(dto -> employeeService.createWithImage(dto, image));
    }

    @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> updateImage(@PathVariable Long id, @RequestPart("file")FilePart file) {
        return employeeService.updateImage(id, file);
    }


}
