package edu.school21.userservice.exception.advice;

import edu.school21.userservice.exception.InvalidJwtTokenException;
import edu.school21.userservice.exception.UserAlreadyExistsException;
import edu.school21.userservice.exception.ValidationRequestException;
import io.envoyproxy.pgv.ValidationException;
import io.grpc.Metadata;
import io.grpc.Status;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.google.rpc.Code.ALREADY_EXISTS;
import static com.google.rpc.Code.INTERNAL;
import static com.google.rpc.Code.INVALID_ARGUMENT;
import static com.google.rpc.Code.NOT_FOUND;
import static com.google.rpc.Code.UNAUTHENTICATED;

@Configuration
public class GrpcExceptionHandlers {

    private static final Metadata.Key<String> ERROR_CODE_METADATA_KEY =
            Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER);

    @Bean
    public GrpcExceptionHandler userAlreadyExistsExceptionHandler() {
        return exception -> {
            if (exception instanceof UserAlreadyExistsException) {
                Metadata metadata = new Metadata();
                metadata.put(ERROR_CODE_METADATA_KEY, ALREADY_EXISTS.name());
                return Status.ALREADY_EXISTS
                        .withDescription(exception.getMessage())
                        .asException(metadata);
            }
            return null;
        };
    }

    @Bean
    public GrpcExceptionHandler entityNotFoundExceptionHandler() {
        return exception -> {
            if (exception instanceof EntityNotFoundException
                    || exception instanceof UsernameNotFoundException) {
                Metadata metadata = new Metadata();
                metadata.put(ERROR_CODE_METADATA_KEY, NOT_FOUND.name());
                return Status.NOT_FOUND
                        .withDescription(exception.getMessage())
                        .asException(metadata);
            }
            return null;
        };
    }

    @Bean
    public GrpcExceptionHandler validationRequestExceptionHandler() {
        return exception -> {
            if (exception instanceof ValidationException
                    || exception instanceof ValidationRequestException) {
                Metadata metadata = new Metadata();
                metadata.put(ERROR_CODE_METADATA_KEY, INVALID_ARGUMENT.name());
                return Status.INVALID_ARGUMENT
                        .withDescription(exception.getMessage())
                        .asException(metadata);
            }
            return null;
        };
    }

    @Bean
    public GrpcExceptionHandler invalidJwtTokenExceptionHandler() {
        return exception -> {
            if (exception instanceof InvalidJwtTokenException) {
                Metadata metadata = new Metadata();
                metadata.put(ERROR_CODE_METADATA_KEY, UNAUTHENTICATED.name());
                return Status.UNAUTHENTICATED
                        .withDescription(exception.getMessage())
                        .asException(metadata);
            }
            return null;
        };
    }

    @Bean
    public GrpcExceptionHandler dataAccessExceptionHandler() {
        return exception -> {
            if (exception instanceof DataAccessException) {
                Metadata metadata = new Metadata();
                metadata.put(ERROR_CODE_METADATA_KEY, INTERNAL.name());
                return Status.INTERNAL
                        .withDescription(exception.getMessage())
                        .asException(metadata);
            }
            return null;
        };
    }
}