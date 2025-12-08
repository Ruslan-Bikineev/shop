package edu.school21.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRqDto {

    @NotNull(message = "first_name: cannot be empty")
    @Size(min = 2, max = 255, message = "first_name must be between 2 and 255 characters")
    @JsonProperty("first_name")
    @Schema(description = "User firstName", example = "Name")
    private String firstName;

    @NotNull(message = "last_name: cannot be empty")
    @Size(min = 2, max = 255, message = "last_name must be between 2 and 255 characters")
    @JsonProperty("last_name")
    @Schema(description = "User lastName", example = "Surname")
    private String lastName;

    @NotNull(message = "password: cannot be empty")
    @Size(min = 5, max = 255, message = "password must be between 5 and 255 characters")
    @Schema(description = "User password", example = "Pass!")
    private String password;

    @NotBlank(message = "mail: cannot be empty")
    @Email(message = "Please provide a valid email address")
    @Size(min = 5, max = 255, message = "mail must be between 2 and 255 characters")
    @Schema(description = "Client mail", example = "John@mail.com")
    private String mail;

    @NotNull(message = "phone_number: cannot be empty")
    @Pattern(
            regexp = "^(8|\\+7)\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$",
            message = "Phone number must be in the format: +7(XXX)XXX-XX-XX or 8(XXX)XXX-XX-XX"
    )
    @JsonProperty("phone_number")
    @Schema(description = "User phone number", example = "+7(123)456-78-90 or 8(123)456-78-90")
    private String phoneNumber;
}
