package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Helper {
    public static String hashString(String input){
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return new String(messageDigest, StandardCharsets.UTF_8);
        } catch (Exception e){
            System.out.println("Unable to hash the input string...");
        }
        return null;
    }


}
