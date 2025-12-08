package edu.school21.userservice.constants;

import io.grpc.Context;
import io.grpc.Metadata;

public class Constant {
    public static final Context.Key<String> AUTHORIZATION_CTX_KEY = Context.key("authorization");
    public static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
}
