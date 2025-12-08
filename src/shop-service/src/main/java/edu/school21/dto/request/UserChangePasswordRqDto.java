package edu.school21.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UserChangePasswordRqDto {
    @JsonProperty("new_password")
    @NotBlank(message = "new_password: cannot be empty")
    @Size(min = 5, max = 255, message = "password must be between 5 and 255 characters")
    @Schema(description = "User new password", example = "newPass!")
    private String newPassword;
}
