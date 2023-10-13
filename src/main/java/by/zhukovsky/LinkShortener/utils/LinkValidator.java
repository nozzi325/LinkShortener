package by.zhukovsky.LinkShortener.utils;

import org.apache.commons.validator.routines.UrlValidator;

public class LinkValidator {
    private static final UrlValidator urlValidator = new UrlValidator();

    public static boolean isValidLink(String link) {
        return urlValidator.isValid(link);
    }
}
