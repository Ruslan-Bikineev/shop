package edu.school21.controllers;

import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.request.UserAuthRqDto;
import edu.school21.dto.request.UserChangePasswordRqDto;
import edu.school21.dto.request.UserPasswordRecoveryRqDto;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.TokenRsDto;
import edu.school21.grpc.UserGrpcClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserGrpcClientService userGrpcClientService;

    @GeneralApiResponses(summary = "Register user")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public TokenRsDto registration(@Valid @RequestBody UserRqDto userRqDto) {
        return userGrpcClientService.registrationUser(userRqDto);
    }

    @GeneralApiResponses(summary = "Authorization user")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/auth")
    public TokenRsDto authorization(@Valid @RequestBody UserAuthRqDto userAuthRqDto) {
        return userGrpcClientService.authorizationUser(userAuthRqDto);
    }

    @GeneralApiResponses(summary = "Change user password")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/change-password")
    public TokenRsDto changeUserPassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @Valid @RequestBody UserChangePasswordRqDto userChangePasswordRqDto) {
        return userGrpcClientService.changeUserPassword(token, userChangePasswordRqDto);
    }

    @GeneralApiResponses(summary = "Send user password to user email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/reset")
    public void passwordRecovery(@Valid @RequestBody UserPasswordRecoveryRqDto userPasswordRecoveryRqDto) {
        userGrpcClientService.passwordRecovery(userPasswordRecoveryRqDto);
    }

    @GeneralApiResponses(summary = "Check authorization user")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/authorization")
    public void isUserAuthorize(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        userGrpcClientService.isUserAuthorize(token);
    }

    @GeneralApiResponses(summary = "Refresh user token")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/refresh")
    public TokenRsDto refreshUserToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return userGrpcClientService.refreshUserToken(token);
    }
}
