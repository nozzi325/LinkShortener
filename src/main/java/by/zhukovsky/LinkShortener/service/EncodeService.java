package by.zhukovsky.LinkShortener.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EncodeService {
    private static final int LENGTH = 7;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Random random = new Random();
    public String generateRandomUrl() {
        char[] result = new char[LENGTH];
        while (true) {
            for (int i = 0; i < LENGTH; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length() - 1);
                result[i] = CHARACTERS.charAt(randomIndex);
            }
            return new String(result);
        }
    }
}
