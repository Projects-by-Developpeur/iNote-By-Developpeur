package com.example.inote;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;

public class Decrypt {
    private static final String ALGORITHM = "AES";

    public static String decrypt(String value, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue, StandardCharsets.UTF_8);
        return decryptedValue;

    }
}
