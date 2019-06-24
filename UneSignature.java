/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package confianceoffline;

import java.nio.file.Files;
import static java.nio.file.Files.list;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Scanner;

/**
 *
 * @author Aurel
 */
public class UneSignature {

    private String data_to_sign;
    private String sign_method;
    private String private_key_file;
    private final String key_algorithm = "RSA";
//    private String sign_algorithm = "SHA1withRSA";

    public String getData_to_sign() {
        return data_to_sign;
    }

    public void setData_to_sign(String data_to_sign) {
        this.data_to_sign = data_to_sign;
    }

    public String getSign_method() {
        return sign_method;
    }

    public void setSign_method(String sign_method) {
        this.sign_method = sign_method;
    }

    public String getPrivate_key_file() {
        return private_key_file;
    }

    public void setPrivate_key_file(String private_key_file) {
        this.private_key_file = private_key_file;
    }

    public UneSignature(String data_to_sign, String sign_method, String private_key_file) {
        this.data_to_sign = data_to_sign;
        this.sign_method = sign_method;
        this.private_key_file = private_key_file;
    }

    public PrivateKey getPrivateKey() throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(this.private_key_file).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(key_algorithm);
        return kf.generatePrivate(spec);
    }

    public byte[] sign() throws NoSuchAlgorithmException, InvalidKeyException, Exception {
        Signature rsa = Signature.getInstance(sign_method);
        rsa.initSign(getPrivateKey());
        rsa.update(this.data_to_sign.getBytes());
        return rsa.sign();
    }

    //Method to write the List of byte[] to a file
    public static void writeBytesToFile(byte[] dat, String filename) throws FileNotFoundException, IOException {
        File f = new File(filename);
        f.getParentFile().mkdirs();
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
        out.write(dat);
//        new BASE64Encoder().encode(signatureBytes)
        out.close();
        System.out.println("Your file is ready.");
    }

    

}
