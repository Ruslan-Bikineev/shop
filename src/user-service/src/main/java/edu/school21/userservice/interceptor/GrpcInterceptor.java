package edu.school21.userservice.interceptor;

import edu.school21.userservice.constants.Constant;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Component
@GlobalServerInterceptor
public class GrpcInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        String bearerToken = headers.get(Constant.AUTHORIZATION_KEY);
        Context context = Context.current().withValue(Constant.AUTHORIZATION_CTX_KEY, bearerToken);
        return Contexts.interceptCall(context, call, headers, next);
    }
}
