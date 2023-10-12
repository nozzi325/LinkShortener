package by.zhukovsky.LinkShortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncodeServiceTest {

    private EncodeService encodeService;

    @BeforeEach
    void setUp() {
        encodeService = new EncodeService();
    }

    @Test
    void generateRandomUrlShouldReturnStringOfCorrectLength() {
        String randomUrl = encodeService.generateRandomUrl();

        assertEquals(7, randomUrl.length());
    }

    @Test
    void generateRandomUrl_ShouldOnlyContainValidCharacters() {
        String randomUrl = encodeService.generateRandomUrl();

        assertTrue(randomUrl.matches("^[a-zA-Z0-9]*$"));
    }

    @Test
    void generateRandomUrl_ShouldGenerateDifferentUrls() {
        String randomUrl1 = encodeService.generateRandomUrl();
        String randomUrl2 = encodeService.generateRandomUrl();

        assertNotEquals(randomUrl1, randomUrl2);
    }
}