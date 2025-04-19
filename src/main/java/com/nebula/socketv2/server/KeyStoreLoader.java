package com.nebula.socketv2.server;

import java.io.InputStream;
import java.security.KeyStore;

public class KeyStoreLoader {

    public static KeyStore loadKeyStore(String resourcePath, String password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");

        try (InputStream inputStream = KeyStoreLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Keystore file not found: " + resourcePath);
            }
            keyStore.load(inputStream, password.toCharArray());
        }

        return keyStore;
    }
}
