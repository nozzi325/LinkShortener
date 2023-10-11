package by.zhukovsky.LinkShortener.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record LinkRequest(
        @NotEmpty(message = "Link field can't be empty")
        @NotNull(message = "Request can't be blank")
        String original
) {
}
