package gpay.com.g_pay;

/**
 * Created by adetunji on 6/1/17.
 */

import org.apache.commons.codec.binary.Base64;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * Created by rajioladayo on 5/3/17.
 */

public class EncryptionHelper {

    public static String encrypt(String message, String secretKey) throws Exception {

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        KeySpec keySpec = new DESedeKeySpec(keyBytes);
        SecretKey key = SecretKeyFactory.getInstance("DESede").generateSecret(keySpec);

//                System.out.println(secretKey.getBytes("utf-8").length);
        //SecretKey key = new SecretKeySpec(keyBytes, "DESede");

        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] plainTextBytes = message.getBytes("utf-8");
        byte[] buf = cipher.doFinal(plainTextBytes);
        byte[] base64Bytes = Base64.encodeBase64(buf);
        String base64EncryptedString = new String(base64Bytes);
        return base64EncryptedString;
    }
}