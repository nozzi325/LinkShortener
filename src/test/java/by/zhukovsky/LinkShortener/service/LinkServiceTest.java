package by.zhukovsky.LinkShortener.service;

import by.zhukovsky.LinkShortener.dto.LinkRequest;
import by.zhukovsky.LinkShortener.dto.StatsResponse;
import by.zhukovsky.LinkShortener.entity.Link;
import by.zhukovsky.LinkShortener.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class LinkServiceTest {
    @Mock
    private EncodeService encodeService;

    @Mock
    private LinkRepository linkRepository;

    private LinkService linkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        linkService = new LinkService(encodeService, linkRepository);
    }

    @Test
    void createShortLink_ShouldGenerateShortLink() {
        LinkRequest linkRequest = new LinkRequest("https://example.com");
        when(linkRepository.existsByOriginalLink(linkRequest.original())).thenReturn(false);
        when(encodeService.generateRandomUrl()).thenReturn("abc123");

        String shortLink = linkService.createShortLink(linkRequest);

        assertEquals("/l/abc123", shortLink);
    }

    @Test
    void createShortLink_ShouldThrowEntityExistsException() {
        LinkRequest linkRequest = new LinkRequest("https://example.com");
        when(linkRepository.existsByOriginalLink(linkRequest.original())).thenReturn(true);

        EntityExistsException exception = assertThrows(EntityExistsException.class,
                () -> linkService.createShortLink(linkRequest));
        assertEquals("Short link for 'https://example.com' already exists", exception.getMessage());
    }

    @Test
    void getOriginalUrl_ShouldReturnOriginalUrlAndIncrementCount() {
        String shortUrl = "/l/abc123";
        Link link = new Link("https://example.com", "abc123");
        when(linkRepository.findByShortLink(shortUrl)).thenReturn(Optional.of(link));

        String originalUrl = linkService.getOriginalUrl(shortUrl);

        assertEquals("https://example.com", originalUrl);
        assertEquals(1, link.getCount());
    }

    @Test
    void getOriginalUrl_ShouldThrowEntityNotFoundException() {
        String shortUrl = "/l/nonexistent";
        when(linkRepository.findByShortLink(shortUrl)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> linkService.getOriginalUrl(shortUrl));
        assertEquals("Requested resource '/l/nonexistent' not found", exception.getMessage());
    }

    @Test
    void getLinkStats_ShouldReturnSingleLinkStats() {
        String shortUrl = "/l/abc123";
        Link link = new Link("https://example.com", "abc123");
        when(linkRepository.findByShortLink(shortUrl)).thenReturn(Optional.of(link));
        when(linkRepository.getRankByCount(link.getCount())).thenReturn(5);

        StatsResponse statsResponse = linkService.getLinkStats(shortUrl);

        assertEquals("/l/abc123", statsResponse.link());
        assertEquals("https://example.com", statsResponse.original());
        assertEquals(6, statsResponse.rank());
        assertEquals(0, statsResponse.count());
    }

    @Test
    void getTotalStats_ShouldReturnStatsInOrder() {
        List<Link> linkList = List.of(
                new Link("https://example1.com", "abc1"),
                new Link("https://example2.com", "abc2"),
                new Link("https://example3.com", "abc3")
        );
        Page<Link> mockPage = new PageImpl<>(linkList);
        when(linkRepository.findAllByOrderByCountDesc(Mockito.any(Pageable.class))).thenReturn(mockPage);

        List<StatsResponse> statsResponses = linkService.getTotalStats(PageRequest.of(0, 10));

        List<StatsResponse> expectedStatsResponses = linkList.stream()
                .map(link -> new StatsResponse(
                        link.getOriginalLink(),
                        "/l/" + link.getShortLink(),
                        linkList.indexOf(link) + 1,
                        link.getCount())
                )
                .collect(Collectors.toList());

        assertEquals(expectedStatsResponses, statsResponses);
    }

    @Test
    void findLinkByShortUrl_ShouldReturnOriginalLink() {
        String shortUrl = "/l/abc123";
        Link link = new Link("https://example.com", "abc123");
        when(linkRepository.findByShortLink(shortUrl)).thenReturn(Optional.of(link));

        Link foundLink = ReflectionTestUtils.invokeMethod(linkService, "findLinkByShortUrl", shortUrl);

        assertEquals(link, foundLink);
    }

    @Test
    void findLinkByNonExistentShortUrl_ShouldThrowEntityNotFoundException() {
        String shortUrl = "/l/nonexistent";
        when(linkRepository.findByShortLink(shortUrl)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> ReflectionTestUtils.invokeMethod(linkService, "findLinkByShortUrl", shortUrl));
        assertEquals("Requested resource '/l/nonexistent' not found", exception.getMessage());
    }
}