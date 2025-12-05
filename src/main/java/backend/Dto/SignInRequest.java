package backend.Dto;

public record SignInRequest(
        String username,
        String password
) {
}
