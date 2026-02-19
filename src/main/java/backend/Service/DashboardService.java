package backend.Service;

import backend.Dto.DashboardResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public interface DashboardService {

    Mono<DashboardResponse> getDashboard(LocalDate startDate, LocalDate endDate);
}
