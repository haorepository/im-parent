package com.hypertech.im.jwt;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class AESSecretUtil {
	/**秘钥的大小*/
    private static final int KEYSIZE = 128;
    
    /**
     * @Description: AES加密
     * @param data - 待加密内容
     * @param key - 加密秘钥
     */
    public static byte[] encrypt(String data, String key) {
        if(StringUtils.isNotBlank(data)){
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                //选择一种固定算法，为了避免不同java实现的不同算法，生成不同的密钥，而导致解密失败
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                random.setSeed(key.getBytes());
                keyGenerator.init(KEYSIZE, random);
                SecretKey secretKey = keyGenerator.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
                // 创建密码器
                Cipher cipher = Cipher.getInstance("AES");
                byte[] byteContent = data.getBytes("utf-8");
                // 初始化
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                byte[] result = cipher.doFinal(byteContent);
                // 加密
                return result; 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @Description: AES加密，返回String
     * @param data - 待加密内容
     * @param key - 加密秘钥
     */
    public static String encryptToStr(String data, String key){

        return StringUtils.isNotBlank(data)
        		?
        	   parseByte2HexStr(encrypt(data, key))
        	    :
        	   null;
    }


    /**
     * @Author: Helon
     * @param data - 待解密字节数组
     * @param key - 秘钥
     */
    public static byte[] decrypt(byte[] data, String key) {
        if (ArrayUtils.isNotEmpty(data)) {
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                //选择一种固定算法，为了避免不同java实现的不同算法，
                //生成不同的密钥，而导致解密失败
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                random.setSeed(key.getBytes());
                keyGenerator.init(KEYSIZE, random);
                SecretKey secretKey = keyGenerator.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
                // 创建密码器
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                // 初始化
                byte[] result = cipher.doFinal(data);
                // 加密
                return result; 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @Description: AES解密，返回String
     * @param enCryptdata - 待解密字节数组
     * @param key - 秘钥
     */
    public static String decryptToStr(String enCryptdata, String key) {
        return StringUtils.isNotBlank(enCryptdata)
        		?
        	   new String(decrypt(parseHexStr2Byte(enCryptdata), key))
        		:
        	   null;
    }

    /**
     * @Description: 将二进制转换成16进制
     * @param buf - 二进制数组
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * @Description: 将16进制转换为二进制
     * @param hexStr - 16进制字符串
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
        String ss = encryptToStr("zhangsan", SecretKeyUtil.ID_KEY);
        System.out.println(ss);
        System.out.println(decryptToStr(ss, SecretKeyUtil.ID_KEY));
    }
}
