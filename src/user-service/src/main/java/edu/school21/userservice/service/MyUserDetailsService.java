package edu.school21.userservice.service;

import edu.school21.userservice.entity.User;
import edu.school21.userservice.entity.auth.UserPrincipal;
import edu.school21.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jspecify.annotations.NonNull;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserPrincipal loadUserByUsername(@NonNull String mail) throws UsernameNotFoundException {
        User user = userRepository.findByMail(mail)
                .orElseThrow(() -> new UsernameNotFoundException("User with mail not found: " + mail));
        return new UserPrincipal(user, Collections.emptyList());
    }
}
