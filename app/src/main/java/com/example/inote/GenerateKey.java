package com.example.inote;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

public class GenerateKey {
    private static final String ALGORITHM = "AES";

    public static Key generateKey(String k) throws Exception {
        Key key = new SecretKeySpec(k.getBytes(), ALGORITHM);
        return key;
    }
}
