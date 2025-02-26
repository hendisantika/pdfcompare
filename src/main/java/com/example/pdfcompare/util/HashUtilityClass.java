package com.example.pdfcompare.util;

import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

@UtilityClass
public class HashUtilityClass {

    public String hashBytes(byte[] data) {

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data);
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (Exception e) {
            return Arrays.toString(data);
        }

    }

}
