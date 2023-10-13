package by.zhukovsky.LinkShortener.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkValidatorTest {

    @Test
    public void testValidLink_ShouldReturnTrue() {
        assertTrue(LinkValidator.isValidLink("https://www.example.com"));
        assertTrue(LinkValidator.isValidLink("http://example.com"));
        assertTrue(LinkValidator.isValidLink("example.com"));
        assertTrue(LinkValidator.isValidLink("https://subdomain.example.co.uk/path/to/page"));
    }

    @Test
    public void testInvalidLink_ShouldReturnFalse() {
        assertFalse(LinkValidator.isValidLink("http://example"));
        assertFalse(LinkValidator.isValidLink("https://example..com"));
        assertFalse(LinkValidator.isValidLink("invalid url"));
    }

}