package edu.school21.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenRsDto(@JsonProperty("access_token") String accessToken) {
}
