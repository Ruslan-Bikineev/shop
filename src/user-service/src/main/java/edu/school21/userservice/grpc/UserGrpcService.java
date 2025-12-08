package edu.school21.userservice.grpc;

import api.edu.school21.proto.grpc.v1.AuthentificationUserRqDto;
import api.edu.school21.proto.grpc.v1.ChangeUserPasswordRqDto;
import api.edu.school21.proto.grpc.v1.CreateUserRqDto;
import api.edu.school21.proto.grpc.v1.JwtTokenRsDto;
import api.edu.school21.proto.grpc.v1.PasswordRecoveryUserRqDto;
import api.edu.school21.proto.grpc.v1.UserGrpc;
import com.google.protobuf.Empty;
import edu.school21.userservice.facade.UserGrpcFacade;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class UserGrpcService extends UserGrpc.UserImplBase {

    private final UserGrpcFacade userGrpcFacade;

    @Override
    public void create(CreateUserRqDto request, StreamObserver<JwtTokenRsDto> responseObserver) {
        log.info("UserGrpcService.create() тело запроса: {}", request);
        JwtTokenRsDto tokenRsDto = userGrpcFacade.create(request);
        responseObserver.onNext(tokenRsDto);
        responseObserver.onCompleted();
    }

    @Override
    public void authentification(AuthentificationUserRqDto request, StreamObserver<JwtTokenRsDto> responseObserver) {
        log.info("UserGrpcService.authentification() тело запроса: {}", request);
        JwtTokenRsDto tokenRsDto = userGrpcFacade.authentification(request);
        responseObserver.onNext(tokenRsDto);
        responseObserver.onCompleted();
    }

    @Override
    public void changePassword(ChangeUserPasswordRqDto request, StreamObserver<JwtTokenRsDto> responseObserver) {
        log.info("UserGrpcService.changePassword() тело запроса: {}", request);
        JwtTokenRsDto tokenRsDto = userGrpcFacade.changePassword(request);
        responseObserver.onNext(tokenRsDto);
        responseObserver.onCompleted();
    }

    @Override
    public void passwordRecovery(PasswordRecoveryUserRqDto request, StreamObserver<Empty> responseObserver) {
        log.info("UserGrpcService.passwordRecovery() тело запроса: {}", request);
        Empty empty = userGrpcFacade.passwordRecovery(request);
        responseObserver.onNext(empty);
        responseObserver.onCompleted();
    }

    @Override
    public void isUserAuthorize(Empty request, StreamObserver<Empty> responseObserver) {
        log.info("UserGrpcService.isUserAuthorize()");
        Empty empty = userGrpcFacade.isUserAuthorize();
        responseObserver.onNext(empty);
        responseObserver.onCompleted();
    }

    @Override
    public void refreshUserToken(Empty request, StreamObserver<JwtTokenRsDto> responseObserver) {
        log.info("UserGrpcService.refreshUserToken()");
        JwtTokenRsDto tokenRsDto = userGrpcFacade.refreshUserToken();
        responseObserver.onNext(tokenRsDto);
        responseObserver.onCompleted();
    }
}
