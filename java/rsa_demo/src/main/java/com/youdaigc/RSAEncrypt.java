package com.youdaigc;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

/**
 * Created by A0003 on 2017-10-30.
 */
public class RSAEncrypt {
    private static final Logger logger = LoggerFactory.getLogger(RSAEncrypt.class);
//    private static String DEFAULT_PUBLIC_KEY = "/rsa-keys/rsa_public_key_2048.txt";
//    private static String DEFAULT_PRIVATE_KEY = "/rsa-keys/rsa_private_key_2048.txt";

    private static String DEFAULT_PUBLIC_KEY = "/rsa-keys/rsa_public_key_1024.txt";
    private static String DEFAULT_PRIVATE_KEY = "/rsa-keys/rsa_private_key_1024.txt";

    //private static int KEY_LENGTH = 2048; // 1024
    private static int KEY_LENGTH = 2048;

    /**
     * 私钥
     */
    private RSAPrivateKey privateKey;

    /**
     * 公钥
     */
    private RSAPublicKey publicKey;

    /**
     * 字节数据转字符串专用集合
     */
    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * 获取私钥
     * @return 当前的私钥对象
     */
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * 获取公钥
     * @return 当前的公钥对象
     */
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * 随机生成密钥对
     */
    public void genKeyPair(){
        KeyPairGenerator keyPairGen= null;
        try {
            keyPairGen= KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        keyPairGen.initialize(KEY_LENGTH, new SecureRandom());
        KeyPair keyPair= keyPairGen.generateKeyPair();
        this.privateKey= (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey= (RSAPublicKey) keyPair.getPublic();
    }

    /**
     * 从文件中输入流中加载公钥
     * @param in 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public void loadPublicKey(InputStream in) throws Exception{
        try {
            BufferedReader br= new BufferedReader(new InputStreamReader(in));
            String readLine= null;
            StringBuilder sb= new StringBuilder();
            while((readLine= br.readLine())!=null){
                if(readLine.charAt(0)=='-'){
                    continue;
                }else{
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPublicKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }


    /**
     * 从字符串中加载公钥
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public void loadPublicKey(String publicKeyStr) throws Exception{
        try {
            BASE64Decoder base64Decoder= new BASE64Decoder();
            byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);
            this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (IOException e) {
            throw new Exception("公钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从文件中加载私钥
     * @param in 私钥文件
     * @return 是否成功
     * @throws Exception
     */
    public void loadPrivateKey(InputStream in) throws Exception{
        try {
            BufferedReader br= new BufferedReader(new InputStreamReader(in));
            String readLine= null;
            StringBuilder sb= new StringBuilder();
            while((readLine= br.readLine())!=null){
                if(readLine.charAt(0)=='-'){
                    continue;
                }else{
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPrivateKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    public void loadPrivateKey(String privateKeyStr) throws Exception{
        try {
            BASE64Decoder base64Decoder= new BASE64Decoder();
            byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);
            PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            this.privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (IOException e) {
            throw new Exception("私钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 加密过程
     * @param publicKey 公钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception{
        if(publicKey== null){
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher= null;
        try {
            cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output= cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 解密过程
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception{
        if (privateKey== null){
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher= null;
        try {
            cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output= cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }


    /**
     * 字节数据转十六进制字符串
     * @param data 输入数据
     * @return 十六进制内容
     */
    public static String byteArrayToString(byte[] data){
        StringBuilder stringBuilder= new StringBuilder();
        for (int i=0; i<data.length; i++){
            //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0)>>> 4]);
            //取出字节的低四位 作为索引得到相应的十六进制标识符
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
            if (i<data.length-1){
                stringBuilder.append(' ');
            }
        }
        return stringBuilder.toString();
    }

    public String loadRsaKeyFile(String fileName){
        StringBuilder result = new StringBuilder();
        try {
            String filePath = getClass().getClassLoader().getResource(".").toURI().getPath() + fileName;
            List<String> fileLines = FileUtils.readLines(new File(filePath), Charset.defaultCharset());

            for(String l : fileLines){
                if(l != null && !l.trim().startsWith("--")){
                    result.append(l.trim());
                }
            }
//            return FileUtils.readFileToString(new File(filePath), Charset.defaultCharset());
        } catch (Exception e) {
            logger.error("加载文件失败：" + e.getMessage(), e);
        }
        return result.toString();
    }

    public void doRsaEncryptTest(){
        long startTime;
        long totalTime = System.currentTimeMillis();
        // 加载公钥
        try {
            startTime = System.currentTimeMillis();
            loadPublicKey(loadRsaKeyFile(RSAEncrypt.DEFAULT_PUBLIC_KEY));
            logger.info(String.format("加载公钥成功 (花费时间:%s ms)", (System.currentTimeMillis() - startTime)));
        } catch (Exception e) {
            logger.error("加载公钥失败" + e.getMessage(), e);
        }

        // 加载私钥
        try {
            startTime = System.currentTimeMillis();
            loadPrivateKey(loadRsaKeyFile(RSAEncrypt.DEFAULT_PRIVATE_KEY));
            logger.info(String.format("加载私钥成功 (花费时间:%s ms)", (System.currentTimeMillis() - startTime)));
        } catch (Exception e) {
            logger.error("加载私钥失败" + e.getMessage(), e);
        }

        //测试字符串
        String encryptStr= "My RSA Demo";

        try {
            // 加密
            startTime = System.currentTimeMillis();
            byte[] cipher = encrypt(getPublicKey(), encryptStr.getBytes());
            logger.info(String.format("\n加密后密文Bytes(长度=%s, 耗时:%s ms): %s\n", cipher.length,
                                        (System.currentTimeMillis() - startTime), RSAEncrypt.byteArrayToString(cipher)));

            // 解密
            startTime = System.currentTimeMillis();
            byte[] plainText = decrypt(getPrivateKey(), cipher);
            logger.info(String.format("\n重新解密后Bytes(长度=%s, 耗时:%s ms): %s\n", plainText.length,
                                        (System.currentTimeMillis() - startTime), RSAEncrypt.byteArrayToString(plainText)));

            logger.info(String.format("\n原始的内容: %s\n解密后明文: %s\n", encryptStr, new String(plainText)));
            logger.info(String.format("完成(RSA-%s)共耗时=%s ms", KEY_LENGTH, (System.currentTimeMillis() - totalTime)));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args){
        RSAEncrypt rsaEncrypt= new RSAEncrypt();
        rsaEncrypt.doRsaEncryptTest();
    }
}
