package backend.Request;

public record Response(
        String accessToken,
        String refreshToken
) {
}
