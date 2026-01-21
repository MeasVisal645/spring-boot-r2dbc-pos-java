package backend.Security;

import backend.Entities.AuditLog;
import backend.Repository.AuditLogRepository;
import backend.Service.UserService;
import backend.Utils.JwtUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Order(-1)
public class AuditLogFilter implements WebFilter {

    private final AuditLogRepository repository;
    private final JwtUtil jwtUtil;

    public AuditLogFilter(AuditLogRepository repository, JwtUtil jwtUtil) {
        this.repository = repository;
        this.jwtUtil = jwtUtil;
    }

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        String param = exchange.getRequest().getURI().toString();
        String ipAddress = getClientIp(exchange);
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        Long userId = getCurrentUserId(exchange);

        return chain.filter(exchange)
                .then(repository.save(AuditLog.builder()
                        .userId(userId)
                        .action(method + " " + path + param)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .timestamp(LocalDateTime.now())
                        .build())
                )
                .then();
    }

    private String getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String realIp = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Real-IP");

        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();
        }

        return "UNKNOWN";
    }



    private Long getCurrentUserId(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUserId(token);
        }
        return null;
    }
}



