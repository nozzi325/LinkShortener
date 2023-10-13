package by.zhukovsky.LinkShortener.utils;

import org.apache.commons.validator.routines.UrlValidator;

public class LinkValidator {
    private static final UrlValidator urlValidator = new UrlValidator();

    public static boolean isValidLink(String link) {
        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            link = "https://" + link;
        }
        return urlValidator.isValid(link);
    }
}
