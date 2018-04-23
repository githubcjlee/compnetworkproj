package com.chat.plugin;

import java.nio.charset.Charset;
import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/*
 * @author Chris Lee
 * 
 */
public class StrongAES 
{
    public String encrypt(String _text , String _key) 
    {
        try 
        {
            String text = _text;
            //String key = "Bar12345Bar12345"; // 128 bit key
            String key = _key;
            while ( key.length() < 16) {
            	key+="X";
            }
            if ( key.length() > 16) key = key.substring(0,16);
            
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes("ISO-8859-1"), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            return new String(encrypted, Charset.forName("ISO-8859-1"));
          
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
        return "Encryption Failed.";
    }
    public String decrypt(String _text , String _key) 
    {
        try 
        {
            String text = _text;
            //String key = "Bar12345Bar12345"; // 128 bit key
            String key = _key;
            while ( key.length() < 16) {
            		key+="X";
            }
            if ( key.length() > 16) key = key.substring(0,16);
            
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes("ISO-8859-1"), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            
            
            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decrypted = cipher.doFinal(text.getBytes("ISO-8859-1"));
            return new String(decrypted);
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
        return "Decryption failed.";
    }
    
    public static void main(String[] args) {
    		StrongAES aes = new StrongAES();
    		AESKeyGeneration gen = new AESKeyGeneration();
    		
    		int rand = 545159512;//new Random().nextInt();
    		
    		System.out.println(rand);
    		String CKA = gen.generateChatKey(""+rand);
    		System.out.println(CKA);
    		
    		String encrypted = aes.encrypt("Hello", CKA);
    		System.out.println(encrypted);

    		String decrypted = aes.decrypt(encrypted, CKA);
    		System.out.println(decrypted);
    		
    }
}