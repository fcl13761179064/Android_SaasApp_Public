 package com.ayla.hotelsaas.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

 public class MD5 {
     private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
             '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

     /**
      * MD5 加密处理
      *
      * @param str
      * @return
      */
     public static String getMD5Str(String str) {

         MessageDigest messageDigest = null;
         try {
             messageDigest = MessageDigest.getInstance("MD5");
             messageDigest.reset();
             messageDigest.update(str.getBytes("UTF-8"));
         } catch (NoSuchAlgorithmException e) {
             System.out.println("NoSuchAlgorithmException caught!");
             System.exit(-1);
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }
         byte[] byteArray = messageDigest.digest();
         StringBuffer md5StrBuff = new StringBuffer();
         for (int i = 0; i < byteArray.length; i++) {
             if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                 md5StrBuff.append("0").append(
                         Integer.toHexString(0xFF & byteArray[i]));
             else
                 md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
         }
         return md5StrBuff.toString();
     }

     public static String toHexString(byte[] b) {
         StringBuilder sb = new StringBuilder(b.length * 2);
         for (int i = 0; i < b.length; i++) {
             sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
             sb.append(HEX_DIGITS[b[i] & 0x0f]);
         }
         return sb.toString();
     }

     public static String md5sum(String filename) {
         InputStream fis = null;
         byte[] buffer = new byte[1024];
         int numRead = 0;
         MessageDigest md5;
         try {
             fis = new FileInputStream(filename);
             md5 = MessageDigest.getInstance("MD5");
             while ((numRead = fis.read(buffer)) > 0) {
                 md5.update(buffer, 0, numRead);
             }
             return toHexString(md5.digest());
         } catch (Exception e) {
             return "";
         } finally {
             if (fis != null) {
                 try {
                     fis.close();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
     }
 }
