package com.srpago.library.api;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Rodolfo on 23/06/2017.
 */

public class Encryptor {
    public static String rsaEncrypt(final String plain) throws Exception {
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
            encryptedBytes = cipher.doFinal(plain.getBytes());
        } catch (Exception e) {
            throw e;
        }

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String s = new String(bytes);
        return s;
    }

    private static String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public static String aesEncrypt(String raw, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return Base64.encodeToString(cipher.doFinal(raw.getBytes()), Base64.DEFAULT);
    }

    public static String getRandomKey() {
        return new BigInteger(160, new SecureRandom()).toString(32);
    }

    private static PublicKey getPublicKey() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
        String key = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAv0utLFjwHQk+1aLjxl9t\\Ojvt/qFD1HfMFzjYa4d3iFKrQtvxaWM/B/6ltPn6+Pez+dOd59zFmzNHg33h8S0p\\aZ6wmNv3mwp4hCJttGzFvl2hhw8Z+OU9KwGSXgQ+5FNyRyDLp0qt75ayvV0vV8oX\\0Pgubd/NTHzRKk0ubXO8WVWkNhMdsv0HGrhIMDXAWLAQBzDewmICVH9MIJzjoZym\\R7AuNpefD4hoVK8cBMjZ0xRKSPyd3zI6uJyERcR3+N9nxvg4guShP27cnD9qpLt4\\L6YtU0BU+husFXoHL6Y2CsxyzxT9mtorAGe5oRiTC7Z/S9u7pxGN4iozgmAei0MZ\\VbKows/qa9/q0PPzbF/PHSZKou1DJvsJ2PKY3ZPYAT7/u4x8NRiJ/6cssuzsIPUd\\Q9HBzA1ZBMHkpOmkipu1G7ks/GwTfQJkHPW5xHu1EOYvgv/PHr3BJnCMNYKFvf5c\\4Qd0COnnU3jDel1OKl7lUzr+ioqUedX393D/fszdK4hjvtUjo6ThTRNm3y4avY/r\\m+oLu8sZWpyBm4PfN2xGOnFco9SiyCT03XOEuOXokid6BDMi0aue9LKJaQR+KGVc\\/H2p2d2Yu4GdgXS1vq1syaf7V0QPOmamTOyJRZ45UoLfBRB8nYBGDo0mPR7GIon6\\M8SmGGsTo3V0L+Ni9bNJHa8CAwEAAQ==\\";
        byte[] byteKey = Base64.decode(key.getBytes("UTF-8"), Base64.DEFAULT);

        X509EncodedKeySpec x509PublicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePublic(x509PublicKey);
    }
}
