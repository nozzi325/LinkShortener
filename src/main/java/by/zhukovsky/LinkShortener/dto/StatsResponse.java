package by.zhukovsky.LinkShortener.dto;

public record StatsResponse(
        String link,
        String original,
        Integer rank,
        Integer count
) {
}
