package edu.school21.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthRqDto {

    @NotBlank(message = "mail: cannot be empty")
    @Size(min = 5, max = 255, message = "mail must be between 2 and 255 characters")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "User mail", example = "John@mail.ru")
    private String mail;

    @NotBlank(message = "password: cannot be empty")
    @Size(min = 5, max = 255, message = "password must be between 5 and 255 characters")
    @Schema(description = "User password", example = "Pass!")
    private String password;
}
