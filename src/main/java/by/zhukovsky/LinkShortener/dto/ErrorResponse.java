package by.zhukovsky.LinkShortener.dto;

import java.time.LocalDateTime;

public record ErrorResponse(String message, LocalDateTime dateAt) {
}
