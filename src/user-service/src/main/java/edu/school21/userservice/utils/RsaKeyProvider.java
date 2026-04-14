package edu.school21.userservice.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
public class RsaKeyProvider {

    private static final String RSA = "RSA";
    private static final int KEY_SIZE = 2048;

    private KeyPair keyPair;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA);
        generator.initialize(KEY_SIZE);
        this.keyPair = generator.generateKeyPair();
    }

    public PrivateKey privateKey() {
        return keyPair.getPrivate();
    }

    public PublicKey publicKey() {
        return keyPair.getPublic();
    }
}

