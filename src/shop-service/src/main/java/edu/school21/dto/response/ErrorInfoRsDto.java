package edu.school21.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ErrorInfoRsDto {
    @Schema(description = "Request path", example = "{url}/api/v1/{api}")
    private String path;

    @Schema(description = "Error message", example = "error message")
    private String message;

    private LocalDateTime timestamp;

    public ErrorInfoRsDto(String path, String message) {
        this.path = path;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}