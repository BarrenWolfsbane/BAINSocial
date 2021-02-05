package tv.bain.bainsocial.backend;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import tv.bain.bainsocial.ICallback;


public class Crypt {
    //Todo: Add AES based PGP Private Key Storage Option For backup purposes

    private ICallback cb;

    public void setCallback(ICallback cb) {
        this.cb = cb;
    }

    public ICallback getCallback() {
        return cb;
    }


    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigestByteArray = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte mDigest : messageDigestByteArray) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & mDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String str2Hex(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = bin.getBytes();
        int bit;
        for (byte b : bs) {
            bit = (b & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = b & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }

    public static String hexToStr(String hex) {
        String digital = "0123456789ABCDEF";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return new String(bytes);
    }

    //region Part of the AES Security
    public static SecretKey generateSecret(String passPhrase) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), salt, 65536, 256); // AES-256
        byte[] key = new byte[0];
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            key = f.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return new SecretKeySpec(key, "AES");
    }

    public static void generateSecret(ICallback cb, String passPhrase) {
        new Thread(() -> {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), salt, 65536, 256); // AES-256
            byte[] key = new byte[0];
            try {
                SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                key = f.generateSecret(spec).getEncoded();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }

            SecretKey secret = new SecretKeySpec(key, "AES");
        }).start();
    }

    public static byte[] aesEncrypt(byte[] data, SecretKey secret) {
        byte[] cipherText = new byte[0];
        try {
            /* The old code was:
             * Cipher cipher = Cipher.getInstance("AES/None/PKCS5Padding");
             * I replaced ECB by None according to this:
             * https://stackoverflow.com/a/36023308
             *  */
            Cipher cipher = Cipher.getInstance("AES/None/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            cipherText = cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return cipherText;
    }


    public static byte[] aesDecrypt(byte[] cipherText, SecretKey secret) {
        byte[] decryptString = new byte[0];
        try {
            /* The old code was:
             * Cipher cipher = Cipher.getInstance("AES/None/PKCS5Padding");
             * I replaced ECB by None according to this:
             * https://stackoverflow.com/a/36023308
             *  */
            Cipher cipher = Cipher.getInstance("AES/None/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            decryptString = cipher.doFinal(cipherText);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return decryptString;
    }

    public static byte[] b64Enc(byte[] bytes) {
        return org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
    }

    public static byte[] b64Dec(String b64) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(b64);
    }
    //endregion

}
