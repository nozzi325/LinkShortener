package by.zhukovsky.LinkShortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkRequest {
    @NotEmpty(message = "Link field can't be empty")
    @NotNull(message = "Request can't be blank")
    private String original;
}
