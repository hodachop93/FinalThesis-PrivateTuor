package com.hodachop93.hohoda.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncodingUtils {

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String encodePassword(String password) {
        MessageDigest digest = null;
        String hash;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes());
            hash = bytesToHexString(digest.digest());

            return Base64.encodeToString(hash.getBytes(), Base64.DEFAULT);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

}
