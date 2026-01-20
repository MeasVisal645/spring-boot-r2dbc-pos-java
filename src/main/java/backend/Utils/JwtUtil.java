package backend.Utils;

import backend.Entities.User;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Data
public class JwtUtil {

    private final Key secretKey;
    private final long jwtExpirationMs;
    private boolean noExpire;

    public JwtUtil(Dotenv dotenv) {
        this.secretKey = Keys.hmacShaKeyFor(dotenv.get("JWT_SECRET").getBytes());
        this.noExpire = Boolean.parseBoolean(dotenv.get("JWT_NO_EXPIRE", "false"));
        this.jwtExpirationMs = noExpire ? Long.MAX_VALUE : Long.parseLong(dotenv.get("JWT_EXPIRATION", "6000000")); // default 600000 = 10 min
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("id", user.getId())
                .claim("role", "ROLE_" + user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public Long extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
