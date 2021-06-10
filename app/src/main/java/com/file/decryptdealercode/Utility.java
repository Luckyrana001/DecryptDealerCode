package com.file.decryptdealercode;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

public class Utility {
    public static String AES_ALGORITHM = "AES";
    public  static String getEncryptedValue(String data,String messageKey){
        // String ypaKey = messageKey.replace("\\", "\\\\");
        Timber.e("data=>" + data);
        String encryptedValue = aesEncryptAndEncode(data, messageKey);
        Timber.e("encrypted data=>" + encryptedValue);
        return encryptedValue;
        // return URLEncoder.encode(encryptedValue);

    }

    public static String aesEncryptAndEncode(String inputString, String key) {
        String encryptedString = null;
        try {
            byte[] decodedKeyArr = Base64Utils.decodeFromString(key);
            SecretKey generatedKey = new SecretKeySpec(decodedKeyArr, 0, decodedKeyArr.length, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generatedKey);
            byte[] inputBytes = cipher.doFinal(inputString.getBytes());
            encryptedString = Base64Utils.encodeToString(inputBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return encryptedString;
    }

    private static byte[] getAppKey(String key) {
        byte[] decodedKeyArr = Base64Utils.decodeFromString(key);
        return decodedKeyArr;
    }
    public static String aesDecodeAndDecrypt(String inputString, String key) {
        String decryptedString = null;
        try {
            byte[] decodedKeyArr = getAppKey(key);
            byte[] decodedInputString = Base64Utils.decodeFromString(inputString);

            SecretKey generatedKey = new SecretKeySpec(decodedKeyArr, 0, decodedKeyArr.length, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generatedKey);
            byte[] inputBytes = cipher.doFinal(decodedInputString);
            decryptedString = new String(inputBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return decryptedString;
    }
}
