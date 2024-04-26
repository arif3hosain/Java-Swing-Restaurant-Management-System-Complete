package com.rms.setting;

import sun.misc.BASE64Decoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Diff {

    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue =
            new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };

    public static String decrypt(String encryptedText)  {
        byte[] decValue = new byte[0];
       try{
          // String encryptedText = "mVJnjVCfG+VURCbaaoRGZbHfL2dXP/irVZS1hhu+l10=";
           Key key = generateKey();
           Cipher c = Cipher.getInstance(ALGORITHM);
           c.init(Cipher.DECRYPT_MODE, key);
           byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedText);
            decValue = c.doFinal(decordedValue);
       }catch (Exception e){
           
       }
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }




    public static String encrypt(String input)
            throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, generateKey());
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }
}
