package edu.school21.userservice.service;

import api.edu.school21.proto.grpc.v1.CreateUserRqDto;
import edu.school21.userservice.entity.User;
import edu.school21.userservice.repository.UserRepository;
import edu.school21.userservice.utils.MapperUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final MapperUtil mapperUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(CreateUserRqDto request) {
        User user = mapperUtil.mapToUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id: %s not found.".formatted(id)));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public String passwordRecovery(String mail) {
        String newPassword = UUID.randomUUID().toString()
                .substring(0, 8);
        User user = userRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("User with mail: %s not found.".formatted(mail)));
        user.setPassword(passwordEncoder.encode(newPassword));
        return newPassword;
    }

    @Transactional(readOnly = true)
    public void checkExistUserByUsername(String mail) {
        if (userRepository.isExistsByMail(mail)) {
            throw new EntityExistsException("User with mail: %s already exists.".formatted(mail));
        }
    }
}
