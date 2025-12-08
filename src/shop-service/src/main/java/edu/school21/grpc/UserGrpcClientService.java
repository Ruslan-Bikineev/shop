package edu.school21.grpc;

import api.edu.school21.proto.grpc.v1.AuthentificationUserRqDto;
import api.edu.school21.proto.grpc.v1.ChangeUserPasswordRqDto;
import api.edu.school21.proto.grpc.v1.CreateUserRqDto;
import api.edu.school21.proto.grpc.v1.JwtTokenRsDto;
import api.edu.school21.proto.grpc.v1.PasswordRecoveryUserRqDto;
import api.edu.school21.proto.grpc.v1.UserGrpc;
import com.google.protobuf.Empty;
import edu.school21.dto.request.UserAuthRqDto;
import edu.school21.dto.request.UserChangePasswordRqDto;
import edu.school21.dto.request.UserPasswordRecoveryRqDto;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.TokenRsDto;
import edu.school21.utils.MapperUtil;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserGrpcClientService {

    private final MapperUtil mapperUtil;
    private final UserGrpc.UserBlockingStub userBlockingStub;
    private static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    public TokenRsDto registrationUser(UserRqDto userRqDto) {
        CreateUserRqDto createUserRqDto = mapperUtil.mapToCreateUserRqDto(userRqDto);
        JwtTokenRsDto tokenRsDto = userBlockingStub.create(createUserRqDto);
        return mapperUtil.mapToTokenRsDto(tokenRsDto);
    }

    public TokenRsDto authorizationUser(UserAuthRqDto userAuthRqDto) {
        AuthentificationUserRqDto authentificationUserRqDto = mapperUtil.mapToAuthentificationUserRqDto(userAuthRqDto);
        JwtTokenRsDto tokenRsDto = userBlockingStub.authentification(authentificationUserRqDto);
        return mapperUtil.mapToTokenRsDto(tokenRsDto);
    }

    public TokenRsDto changeUserPassword(String token,
                                         UserChangePasswordRqDto userChangePasswordRqDto) {
        ChangeUserPasswordRqDto changeUserPasswordRqDto = mapperUtil.mapToChangeUserPasswordRqDto(userChangePasswordRqDto);
        JwtTokenRsDto tokenRsDto = stubWithAuth(token).changePassword(changeUserPasswordRqDto);
        return mapperUtil.mapToTokenRsDto(tokenRsDto);
    }

    public void passwordRecovery(UserPasswordRecoveryRqDto userPasswordRecoveryRqDto) {
        PasswordRecoveryUserRqDto passwordRecoveryUserRqDto = mapperUtil.mapToPasswordRecoveryUserRqDto(userPasswordRecoveryRqDto);
        userBlockingStub.passwordRecovery(passwordRecoveryUserRqDto);
    }

    public void isUserAuthorize(String token) {
        stubWithAuth(token).isUserAuthorize(Empty.getDefaultInstance());
    }

    public TokenRsDto refreshUserToken(String token) {
        JwtTokenRsDto tokenRsDto = stubWithAuth(token).refreshUserToken(Empty.getDefaultInstance());
        return mapperUtil.mapToTokenRsDto(tokenRsDto);
    }

    private UserGrpc.UserBlockingStub stubWithAuth(String token) {
        Metadata metadata = new Metadata();
        metadata.put(AUTHORIZATION_KEY, token);
        ClientInterceptor interceptor = MetadataUtils.newAttachHeadersInterceptor(metadata);
        return userBlockingStub.withInterceptors(interceptor);
    }
}
