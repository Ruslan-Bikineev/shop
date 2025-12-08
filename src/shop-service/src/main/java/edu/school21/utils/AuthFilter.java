package edu.school21.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.school21.dto.response.ErrorInfoRsDto;
import edu.school21.exceptions.advice.ExceptionControllerAdvice;
import edu.school21.grpc.UserGrpcClientService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final UserGrpcClientService userGrpcClientService;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_NOT_FOUND_MESSAGE = "Bearer token is empty";
    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/api/v1/clients",
            "/api/v1/images",
            "/api/v1/products",
            "/api/v1/suppliers",
            "/api/v1/users/refresh",
            "/api/v1/users/authorization",
            "/api/v1/users/change-password"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!isProtectedPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            log.debug("AuthFilter.doFilterInternal(): Token not found, token: {}", authHeader);
            ErrorInfoRsDto errorInfoRsDto = new ErrorInfoRsDto(
                    request.getRequestURL().toString(),
                    TOKEN_NOT_FOUND_MESSAGE);
            writeUnauthorizedResponse(response, errorInfoRsDto);
            return;
        }
        try {
            userGrpcClientService.isUserAuthorize(authHeader);
            filterChain.doFilter(request, response);
        } catch (StatusRuntimeException ex) {
            log.debug("AuthGatewayFilter.doFilterInternal().StatusRuntimeException: status: {} message:{}",
                    ex.getStatus(), ex.getMessage());
            ErrorInfoRsDto errorInfoRsDto = new ErrorInfoRsDto(
                    request.getRequestURL().toString(),
                    ex.getStatus().getDescription()
            );
            writeServerExceptionResponse(response, errorInfoRsDto, ex.getStatus());
        }
    }

    private boolean isProtectedPath(String requestUri) {
        return PROTECTED_PATHS.stream()
                .anyMatch(requestUri::startsWith);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response,
                                           ErrorInfoRsDto errorInfoRsDto) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorInfoRsDto));
        response.getWriter().flush();
    }

    private void writeServerExceptionResponse(HttpServletResponse response,
                                              ErrorInfoRsDto errorInfoRsDto,
                                              Status status) throws IOException {
        HttpStatus httpStatus = ExceptionControllerAdvice.mapGrpcStatusToHttp(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(httpStatus.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorInfoRsDto));
        response.getWriter().flush();
    }
}
