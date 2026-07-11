package edu.school21.userservice.repository;

import api.edu.school21.proto.grpc.v1.CreateUserRqDto;
import edu.school21.userservice.exception.UserAlreadyExistsException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserJDBCRepository {

    private static final int BATCH_SIZE_10K = 10_000;
    private static final String BATCH_INSERT_WITH_ON_CONFLICT_MAIL = """
            INSERT INTO users (first_name, last_name, password, mail, phone_number)
            ON CONFLICT (mail) DO NOTHING
            """;
    private static final String INSERT_WITH_ON_CONFLICT_MAIL = """
            INSERT INTO users
                (first_name, last_name, password, mail, phone_number)
            VALUES
                (?, ?, ?, ?, ?)
            ON CONFLICT (mail) DO NOTHING
            RETURNING id
            """;


    private final JdbcTemplate jdbcTemplate;

    public Long saveWithOnConflictMail(CreateUserRqDto createUserRqDto, String passwordEncoded) throws UserAlreadyExistsException {
        try {
            return jdbcTemplate.queryForObject(INSERT_WITH_ON_CONFLICT_MAIL, Long.class, createUserRqDto.getFirstName(),
                    createUserRqDto.getFirstName(), passwordEncoded, createUserRqDto.getFirstName(), createUserRqDto.getPhoneNumber());
        } catch (EmptyResultDataAccessException _) {
            throw new UserAlreadyExistsException("User with mail: %s already exists.".formatted(createUserRqDto.getMail()));
        }
    }

    public int batchSaveWithOnConflictMail(@Nullable List<CreateUserRqDto> createUserRqDtos,
                                           Map<String, String> passwordEncodedByMail) {
        int savedCount = 0;
        if (!CollectionUtils.isEmpty(createUserRqDtos)) {
            int[][] batches = jdbcTemplate.batchUpdate(
                    BATCH_INSERT_WITH_ON_CONFLICT_MAIL,
                    createUserRqDtos,
                    BATCH_SIZE_10K,
                    (ps, createUserRqDto) -> {
                        ps.setString(1, createUserRqDto.getFirstName());
                        ps.setString(2, createUserRqDto.getLastName());
                        ps.setString(3, passwordEncodedByMail.get(createUserRqDto.getMail()));
                        ps.setString(4, createUserRqDto.getMail());
                        ps.setString(5, createUserRqDto.getPhoneNumber());
                    }
            );
            savedCount = calculateTotalSavedEntities(batches);
        }
        return savedCount;
    }

    private int calculateTotalSavedEntities(int[][] batches) {
        int totalCount = 0;
        for (int[] batch : batches) {
            for (int count : batch) {
                totalCount += count;
            }
        }
        return totalCount;
    }
}