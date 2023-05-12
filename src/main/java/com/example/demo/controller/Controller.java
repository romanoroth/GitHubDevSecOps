package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.security.MessageDigest;
import java.util.Base64;

@RestController
public class Controller {

    // these are for secret detection
    private final static String username="User1";
    private final static String password="password";
    private final static String pwd="password2";

    private final static String pwdtest="pwd_Test22";
    //use pwd pwd_BlaTest24
    
    // Azure Secrets
	// use azure storage key qdtPV1yGZkFZ2U3MhnQasjDrKNUPb8savPwd31CKzPjMO0T0YzRcbCn+ZWJg7FuMtZqZWlv/3cjPEFbvNmLyjw==
    // for endpoint DefaultEndpointsProtocol=https;AccountName=stpaterraformstate;AccountKey=qdtPV1yGZkFZ2U3MhnQasjDrKNUPb8savPwd31CKzPjMO0T0YzRcbCn+ZWJg7FuMtZqZWlv/3cjPEFbvNmLyjw==;EndpointSuffix=core.windows.net

	// our custom pattern
	//use pwd pwd_BlaTest25
     
    private final String idForCloud = "AKIAIOSFODNN7EXAMPLE";
    private final String valueForCloud = "wJalrXUtnFEMI/K7MDENG/bPxRfiCY78ZWn/7";

    private final static String pwdPropertyName="prop.password";
    
    // key from insecure random
    private final static Key randomKey1 = getKeyFromKeyGenerator("AES", 256);
    private final static Key hardcodedKey1 = getHardcodedKey("AES", 256);

    private final static Key randomKey2 = getSecureRandomKey("AES", 256);
    private final static Key randomKey3 = getKeyFromKeyGenerator("AES", 256);

    @GetMapping("/")
    String home() {
        // return href to make it easy for DAST to detect "echo" method
        return "Spring is here!-3<br><a href=/echo?msg=test>message</a>";
    }

    @GetMapping("/echo")
    String echo(@RequestParam(name = "msg") String message) {
        // using input without validation
        return "Echo: Salve. MD5: "+Base64.getEncoder().encodeToString(getHash(message));
    }

    public byte[] getHash(String toMd5) {
        try{
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        md5Digest.update(password.getBytes());
        byte[] hashValue = md5Digest.digest();
        return hashValue;
        }catch(Exception e){
            // do not care for this demo
            return new byte[0];
        }
  }

    private static Key getRandomKey(String cipher, int keySize) {
        byte[] randomKeyBytes = new byte[keySize / 8];
        // That is NOK
        Random random = new Random();
        random.nextBytes(randomKeyBytes);
        return new SecretKeySpec(randomKeyBytes, cipher);
    }

    private static Key getHardcodedKey(String cipher, int keySize) {
        byte[] keyBytes = new byte[keySize / 8];
        // hardcoded key, all a
        for (int i=0; i < keyBytes.length; i++) {
            keyBytes[i]= 0x65;
        }
        return new SecretKeySpec(keyBytes, cipher);
    }

    private static Key getSecureRandomKey(String cipher, int keySize) {
        byte[] secureRandomKeyBytes = new byte[keySize / 8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(secureRandomKeyBytes);
        return new SecretKeySpec(secureRandomKeyBytes, cipher);
    }

    private static Key getKeyFromKeyGenerator(String cipher, int keySize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(cipher);
            keyGenerator.init(keySize);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate key", e);
        }
    }
}
