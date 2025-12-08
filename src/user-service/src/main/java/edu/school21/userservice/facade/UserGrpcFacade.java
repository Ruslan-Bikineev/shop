package edu.school21.userservice.facade;

import api.edu.school21.proto.grpc.v1.AuthentificationUserRqDto;
import api.edu.school21.proto.grpc.v1.ChangeUserPasswordRqDto;
import api.edu.school21.proto.grpc.v1.CreateUserRqDto;
import api.edu.school21.proto.grpc.v1.JwtTokenRsDto;
import api.edu.school21.proto.grpc.v1.PasswordRecoveryUserRqDto;
import com.google.protobuf.Empty;
import edu.school21.userservice.constants.Constant;
import edu.school21.userservice.entity.User;
import edu.school21.userservice.entity.auth.UserPrincipal;
import edu.school21.userservice.exception.ValidationRequestException;
import edu.school21.userservice.service.NotificationService;
import edu.school21.userservice.service.UserService;
import edu.school21.userservice.utils.JwtUtil;
import io.envoyproxy.pgv.ValidationException;
import io.envoyproxy.pgv.ValidatorIndex;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserGrpcFacade {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ValidatorIndex validatorIndex;
    private final NotificationService notificationService;
    private final AuthenticationManager authenticationManager;

    public JwtTokenRsDto create(CreateUserRqDto request) {
        validateRequest(request);
        userService.checkExistUserByUsername(request.getMail());
        User user = userService.save(request);
        String token = jwtUtil.generateToken(user.getId());
        return JwtTokenRsDto.newBuilder()
                .setToken(token)
                .build();
    }

    public JwtTokenRsDto authentification(AuthentificationUserRqDto request) {
        validateRequest(request);
        UserPrincipal userPrincipal = authenticate(request.getMail(), request.getPassword());
        String token = jwtUtil.generateToken(userPrincipal.getId());
        return JwtTokenRsDto.newBuilder()
                .setToken(token)
                .build();
    }

    public JwtTokenRsDto changePassword(ChangeUserPasswordRqDto request) {
        validateRequest(request);
        String bearerToken = Constant.AUTHORIZATION_CTX_KEY.get();
        Claims claims = jwtUtil.parseAndValidateToken(bearerToken);
        User user = userService.changePassword(Long.valueOf(claims.getSubject()), request.getNewPassword());
        String token = jwtUtil.generateToken(user.getId());
        return JwtTokenRsDto.newBuilder()
                .setToken(token)
                .build();
    }

    @Transactional
    public Empty passwordRecovery(PasswordRecoveryUserRqDto request) {
        validateRequest(request);
        String newPassword = userService.passwordRecovery(request.getMail());
        notificationService.sendToEmail(request.getMail(), newPassword);
        return Empty.getDefaultInstance();
    }

    public Empty isUserAuthorize() {
        String bearerToken = Constant.AUTHORIZATION_CTX_KEY.get();
        jwtUtil.parseAndValidateToken(bearerToken);
        return Empty.getDefaultInstance();
    }

    public JwtTokenRsDto refreshUserToken() {
        String bearerToken = Constant.AUTHORIZATION_CTX_KEY.get();
        String userIdFromToken = jwtUtil.getUserIdFromToken(bearerToken);
        String token = jwtUtil.generateToken(Long.valueOf(userIdFromToken));
        return JwtTokenRsDto.newBuilder()
                .setToken(token)
                .build();
    }

    private UserPrincipal authenticate(String mail, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(mail, password));
        return (UserPrincipal) authentication.getPrincipal();
    }

    private void validateRequest(Object request) {
        try {
            validatorIndex.validatorFor(request.getClass())
                    .assertValid(request);
        } catch (ValidationException ex) {
            throw new ValidationRequestException(ex.getMessage());
        }
    }
}
