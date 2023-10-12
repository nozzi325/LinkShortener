package by.zhukovsky.LinkShortener.dto;

import javax.validation.constraints.NotEmpty;

public record LinkRequest(
        @NotEmpty(message = "Link field can't be empty")
        String original
) {
}
