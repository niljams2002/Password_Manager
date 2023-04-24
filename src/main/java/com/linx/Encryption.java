package com.linx;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
    public static final String KEY = "0123456789abcdef0123456789abcdef";
    public static final String INIT_VECTOR = "abcdefghijklmnop";


    /**
     *
     * @param str The string that is to be encoded
     * @return  SHA-256 encoded string
     * @throws NoSuchAlgorithmException In case the SHA256 algorithm does not exist
     */
    public String encrypt_SHA256(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));

        StringBuilder stringBuilder = new StringBuilder();

        for (byte b: hash){
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param str The string that is to be encoded
     * @return encoded string
     * @throws NoSuchPaddingException Incorrect padding
     * @throws NoSuchAlgorithmException Algorithm not found
     * @throws InvalidAlgorithmParameterException no idea what this means
     * @throws InvalidKeyException Key is not correct
     * @throws IllegalBlockSizeException no idea what this is
     * @throws BadPaddingException init vector and/or key have bad padding
     */
    public String encrypt_AES256(String str) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] ivBytes = INIT_VECTOR.getBytes(StandardCharsets.UTF_8);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

        byte[] hash = cipher.doFinal(str.getBytes());

        StringBuilder stringBuilder = new StringBuilder();

        for (byte b: hash){
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param encrypted the encoded string that has to be decoded
     * @return  The decoded string
     * @throws NoSuchPaddingException Incorrect padding
     * @throws NoSuchAlgorithmException Algorithm not found
     * @throws InvalidAlgorithmParameterException no idea what this means
     * @throws InvalidKeyException Key is not correct
     * @throws IllegalBlockSizeException no idea what this is
     * @throws BadPaddingException init vector and/or key have bad padding
     */
    public String decrypt_AES256(String encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] ivBytes = INIT_VECTOR.getBytes(StandardCharsets.UTF_8);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

        byte[] hash = new byte[encrypted.length() / 2];
        for (int i = 0; i < hash.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(encrypted.substring(index, index + 2), 16);
            hash[i] = (byte) j;
        }

        byte[] decrypted = cipher.doFinal(hash);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
