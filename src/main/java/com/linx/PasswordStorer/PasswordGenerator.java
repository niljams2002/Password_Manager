package com.linx.PasswordStorer;
import java.util.Random;

public class PasswordGenerator {
    // THANK YOU Python3 <3
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
    private static final int min = 10;
    private static final int max = 15;

    /**
     * Generate a random string whose length is bounded between 10 and 15
     * String consists of Alphabets, Numbers and Special Characters.
     * @return randomly generated string
     */
    public String generateRandomString(){
        Random random = new Random();
        int length = random.nextInt(max - min + 1) + min; // choose random length between max and min

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++){
            int randomIndex = random.nextInt(characters.length());  // get a random index
            char randomChar = characters.charAt(randomIndex);   // use that random index to get a character from the preset string "characters"
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }


}
