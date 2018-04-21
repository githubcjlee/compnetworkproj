package com.chat.plugin;

import javax.swing.*;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

class AESKeyGeneration 
{
   byte[] skey = new byte[1000];
   String skeyString;
   static byte[] raw;

   public AESKeyGeneration() 
   {
      generateSymmetricKey();
   }
   void generateSymmetricKey() 
   {
   try 
      {
      Random r = new Random();
      int num = r.nextInt(10000);
      String knum = String.valueOf(num);
      byte[] knumb = knum.getBytes();
      skey=getRawKey(knumb);
      skeyString = new String(skey);
      System.out.println("AES Symmetric key = "+skeyString);
      }
   catch(Exception e) 
   {
      System.out.println(e);
   }
}

private static byte[] getRawKey(byte[] seed) throws Exception 
{
   KeyGenerator kgen = KeyGenerator.getInstance("AES");
   SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
   sr.setSeed(seed);
   kgen.init(128, sr); // 192 and 256 bits may not be available
   SecretKey skey = kgen.generateKey();
   raw = skey.getEncoded();
   return raw;
}
public byte[] getSessionKey() 
{
   return raw;
}
public static void main(String args[]) 
{
   AESKeyGeneration aeskey = new AESKeyGeneration();
}
}