package com.gg.midend.utils;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.hutool.core.codec.Base64;

public class YQSign {

    /**
     * 描述：Aes加密
     *
     */
    public static String aesEncode(String content) {

        try {
            // 加密模式/补码方式，固定值，不允许修改
            String CIPHER_MODEL = "/CBC/NoPadding";
            // 加密算法，固定值，不允许修改
            String keyAlgorithm = "AES";
            // 密钥，从医院获取
            String key = "VRGgsLzedKwUkdfeIlfXXQ==";

            // 将密钥进行base64解密
            byte[] keys = Base64.decode(key);

            // 按照 算法/模式/补码方式  构建Cipher对象
            Cipher cipher;
            cipher = Cipher.getInstance(keyAlgorithm + CIPHER_MODEL);

            // 获取块的大小
            int blockSize = cipher.getBlockSize();
            // 获取待加密数据长度大小
            int plaintextLength = content.getBytes(Charset.forName("utf-8")).length;
            // 计算待加密数据长度缺失大小
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] contentBytes = content.getBytes(Charset.forName("utf-8"));
            byte[] plaintext = new byte[plaintextLength];
            // 待加密内容长度不足时，用空补充长度
            System.arraycopy(contentBytes, 0, plaintext, 0, contentBytes.length);
            // 生成密匙
            SecretKeySpec keyspec = new SecretKeySpec(keys, keyAlgorithm);
            // 构建偏移量，使用密钥作为偏移量
            IvParameterSpec ivspec = new IvParameterSpec(keys);
            // 用密钥和一组算法参数初始化此 Cipher
            // Cipher.ENCRYPT_MODE用于将 Cipher 初始化为加密模式的常量
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            // 将数据进行加密，得到密文
            byte[] encryped = cipher.doFinal(plaintext);

            // 转base64字符串，encryptContent 为最终密文
            String encryptContent = Base64.encode(encryped);

            return encryptContent;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 描述：Aes解密
     *
     */
    public static String aesDecode(String content) {

        try {
            // 加密模式/补码方式，固定值，不允许修改
            String CIPHER_MODEL = "/CBC/NoPadding";
            // 加密算法，固定值，不允许修改
            String keyAlgorithm = "AES";
            // 密钥，从医院获取
            String key = "VRGgsLzedKwUkdfeIlfXXQ==";

            // 将密钥进行base64解密
            byte[] keys = Base64.decode(key);

            // 转base64字节码
            byte[] contentBytes = Base64.decode(content);
            // 按照 算法/模式/补码方式  构建Cipher对象
            Cipher cipher = Cipher.getInstance(keyAlgorithm + CIPHER_MODEL);
            // 生成密匙
            SecretKeySpec keyspec = new SecretKeySpec(keys, keyAlgorithm);
            // 构建偏移量，使用密钥作为偏移量
            IvParameterSpec ivspec = new IvParameterSpec(keys);
            // 用密钥和一组算法参数初始化此 Cipher
            // Cipher.ENCRYPT_MODE用于将 Cipher 初始化为解密模式的常量
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            // 将密文进行解密，得到解密后数据
            byte[] result = cipher.doFinal(contentBytes);
            // decodeContent 为最终解密后的内容
            String decodeContent = new String(result, Charset.forName("utf-8")).trim();

            return decodeContent;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 描述：源启sign签名
     *
     */
    public static String sign(String content) {

        try {
            char[] chars = "0123456789ABCDEF".toCharArray();
            byte[] con = content.getBytes("utf-8");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < con.length; i++) {
              int bit = (con[i] & 0xF0) >> 4;
              sb.append(chars[bit]);
              bit = con[i] & 0xF;
              sb.append(chars[bit]);
            } 
            // 转换十六进制后的待签名内容
            content = sb.toString();
            MD5Digest md5Digest = new MD5Digest();
            md5Digest.reset();
            md5Digest.update(content.getBytes("utf-8"), 0, content.length());
            byte[] resByte = new byte[16];
            md5Digest.doFinal(resByte, 0);
            System.out.println("MD5=" + new String(resByte, "utf-8"));
            // 将签名用base64加密，res即为最终的签名结果
            return new String(Base64.encode(resByte));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println(aesEncode("测试数据加密"));
        System.out.println(aesDecode("Vt8aB5GzOydExhOKTZzb2CxaOX3x0j3H2PlqM6quiIlC8/V16JSAGlmzEl57dRSxlmkSRJEXxj8xX8RNlTn73XDKY/6WgfPaTMJhtmcqPxblmRdIziorN8G7/zDgQU0weeo0+jznMfQOoZNy/7fNNOJ5KPY4tA13e25pNGIfk1sB6+/M4z1CSSqiNxJHZyqIotvjX9+gVQlyHMT/0oUioHbjSuB4OHC/fMjMHmICXkiVB+mazMT3u48MX2/IUZfKAGCV3wxOrZR5WVgwpSTuYw=="));
        System.out.println(sign("{\"result\":{\"sign_type\":\"md5\",\"sign\":\"\",\"code\":\"10000\",\"msg\":\"接口调用成功，并且业务系统也处理成功\"},\"body\":\"B06yK+h55/LYcgcjeanAn8aLV28ALqOa0IhUpNq2X9I=\"}"));
    }

}
