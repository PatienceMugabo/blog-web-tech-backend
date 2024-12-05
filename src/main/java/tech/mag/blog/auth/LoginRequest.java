package tech.mag.blog.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "a valid email is required")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "A valid password is needed")
    @Size(min = 4, max = 50, message = "password must be between 5 and 50 characters")
    private String password;
}
