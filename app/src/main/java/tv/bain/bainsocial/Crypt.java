package tv.bain.bainsocial;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    //Todo: Add AES based PGP Private Key Storage Option For backup purposes
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
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
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
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
    //Part of the AES Security
    public static SecretKey generateSecret(String passPhrase) {
        SecretKey secret = new SecretKeySpec(passPhrase.getBytes(), "AES");
        return secret;
    }
    public static byte[] aesEncrypt(String message, SecretKey secret) {
        /* Encrypt the message. */
        Cipher cipher = null;
        try { cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        catch (NoSuchPaddingException e) { e.printStackTrace(); }

        try { cipher.init(Cipher.ENCRYPT_MODE, secret); }
        catch (InvalidKeyException e) { e.printStackTrace(); }

        byte[] cipherText = new byte[0];
        try { cipherText = cipher.doFinal(message.getBytes("UTF-8")); }
        catch (BadPaddingException e) { e.printStackTrace(); }
        catch (IllegalBlockSizeException e) { e.printStackTrace(); }
        catch (UnsupportedEncodingException e) { e.printStackTrace(); }
        return cipherText;
    }
    public static String aesDecrypt(byte[] cipherText, SecretKey secret) {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        try { cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        catch (NoSuchPaddingException e) { e.printStackTrace(); }

        try { cipher.init(Cipher.DECRYPT_MODE, secret); }
        catch (InvalidKeyException e) { e.printStackTrace(); }

        String decryptString = null;
        try { decryptString = new String(cipher.doFinal(cipherText), "UTF-8"); }
        catch (UnsupportedEncodingException e) { e.printStackTrace(); }
        catch (BadPaddingException e) { e.printStackTrace(); }
        catch (IllegalBlockSizeException e) { e.printStackTrace(); }
        return decryptString;
    }
}
