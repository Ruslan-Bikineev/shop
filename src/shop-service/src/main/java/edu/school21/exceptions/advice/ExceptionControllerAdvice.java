package edu.school21.exceptions.advice;

import edu.school21.dto.response.ErrorInfoRsDto;
import edu.school21.exceptions.EmptyFileException;
import edu.school21.exceptions.InsufficientStockException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorInfoRsDto handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ErrorInfoRsDto(req.getRequestURL().toString(), errors.values().toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorInfoRsDto handleMethodArgumentTypeMismatchException(HttpServletRequest req, MethodArgumentTypeMismatchException e) {
        String error = e.getName() + " should be of type " + e.getRequiredType().getName();
        return new ErrorInfoRsDto(req.getRequestURL().toString(), error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestPartException.class,
            MissingServletRequestParameterException.class,
            EmptyFileException.class
    })
    public ErrorInfoRsDto handleBadRequestExceptions(HttpServletRequest req, Exception e) {
        return new ErrorInfoRsDto(
                req.getRequestURL().toString(),
                e.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfoRsDto handleDataIntegrityViolation(HttpServletRequest req, DataIntegrityViolationException e) {
        String message = e.getRootCause().getMessage().split("Подробности: ")[1];
        return new ErrorInfoRsDto(req.getRequestURL().toString(), message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorInfoRsDto handleConstraintViolationException(HttpServletRequest req, ConstraintViolationException e) {
        StringBuilder sb = new StringBuilder();
        e.getConstraintViolations().forEach(violation -> {
            String paramName = violation.getPropertyPath().toString();
            if (paramName.contains(".")) {
                paramName = paramName.substring(paramName.lastIndexOf('.') + 1);
            }
            sb.append(paramName).append(": ").append(violation.getMessage()).append("; ");
        });
        return new ErrorInfoRsDto(req.getRequestURL().toString(), sb.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientStockException.class)
    public ErrorInfoRsDto handleInsufficientStockException(HttpServletRequest req, InsufficientStockException ex) {
        return new ErrorInfoRsDto(req.getRequestURL().toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorInfoRsDto handleEntityNotFoundException(HttpServletRequest req, EntityNotFoundException e) {
        return new ErrorInfoRsDto(req.getRequestURL().toString(), e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorInfoRsDto handleHttpRequestMethodNotSupportedException(HttpServletRequest req,
                                                                       HttpRequestMethodNotSupportedException e) {
        return new ErrorInfoRsDto(req.getRequestURL().toString(), e.getMessage());
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorInfoRsDto> handleStatusRuntimeException(HttpServletRequest req, StatusRuntimeException e) {
        Status status = e.getStatus();
        HttpStatus httpStatus = mapGrpcStatusToHttp(status);
        ErrorInfoRsDto dto = new ErrorInfoRsDto(
                req.getRequestURL().toString(),
                Objects.requireNonNullElse(status.getDescription(), String.format("gRPC error: %s", status))
        );
        return ResponseEntity.status(httpStatus)
                .body(dto);
    }

    public static HttpStatus mapGrpcStatusToHttp(Status grpcStatus) {
        return switch (grpcStatus.getCode()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case DEADLINE_EXCEEDED -> HttpStatus.GATEWAY_TIMEOUT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
