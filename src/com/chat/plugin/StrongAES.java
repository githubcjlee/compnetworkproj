package com.chat.plugin;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            return new String(encrypted);
          
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
        return "Encryption failed.";
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
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            
            byte[] encrypted = _text.getBytes();
            
            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(encrypted));
            return new String(decrypted);
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
        return "Decryption failed.";
    }
    public static void main(String[] args) 
    {
        StrongAES app = new StrongAES();
       // app.run();
    }
}