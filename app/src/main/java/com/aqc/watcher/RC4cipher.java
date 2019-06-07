package com.aqc.watcher;
//https://stackoverflow.com/questions/12289717/rc4-encryption-java

import java.util.Formatter;

public class RC4cipher {
    private final byte[] S = new byte[256];
    private final byte[] T = new byte[256];

    public RC4cipher() {

    }

    public RC4cipher(final byte[] key) {
        generateKeyStream(key);
    };

    public String encryptString(String key_str,String text_str) {
        generateKeyStream(key_str.getBytes());
        byte[] output = encrypt(text_str.getBytes());
        return bytesToHexString(output);
    }

    public void generateKeyStream(byte[] cipher_key) {
        int keylen;
        if (cipher_key.length < 1 || cipher_key.length > 256) {
            throw new IllegalArgumentException(
                    "key must be between 1 and 256 bytes");
        } else {
            keylen = cipher_key.length;
            for (int i = 0; i < 256-1; i++) {
                S[i] = (byte) i;
                T[i] = cipher_key[i % keylen];
            }
            int j = 0;
            byte tmp;
            for (int i = 0; i < 256-1; i++) {
                j = (j + S[i] + T[i]) & 0xFF;
                tmp = S[j];
                S[j] = S[i];
                S[i] = tmp;
            }
        }
    }

    public byte[] encrypt(final byte[] plaintext) {
        final byte[] ciphertext = new byte[plaintext.length];
        int i = 0, j = 0, k, t;
        byte tmp;
        for (int counter = 0; counter < plaintext.length; counter++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            tmp = S[j];
            S[j] = S[i];
            S[i] = tmp;
            t = (S[i] + S[j]) & 0xFF;
            k = S[t];
            ciphertext[counter] = (byte) (plaintext[counter] ^ k);
        }
        return ciphertext;
    }

    public byte[] decrypt(final byte[] ciphertext) {
        return encrypt(ciphertext);
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return sb.toString();
    }

}