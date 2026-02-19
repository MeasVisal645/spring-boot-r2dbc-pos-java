package backend.ServiceImpl;

import backend.Dto.DashboardResponse;
import backend.Repository.CustomerRepository;
import backend.Repository.OrderDetailRepository;
import backend.Repository.OrderItemRepository;
import backend.Repository.ProductRepository;
import backend.Service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    @Override
    public Mono<DashboardResponse> getDashboard(LocalDate startDate, LocalDate endDate) {
        return Mono.zip(
                customerRepository.countCustomers(),
                orderDetailRepository.sumTotalSalesByDate(startDate, endDate),
                orderItemRepository.countOrderItemByDate(startDate, endDate),
                productRepository.countProducts()
        ).map(t -> new DashboardResponse(
                t.getT1(),
                t.getT2(),
                t.getT3(),
                t.getT4()
        ));
    }

}
