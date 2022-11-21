package by.zhukovsky.LinkShortener.service;

import by.zhukovsky.LinkShortener.dto.LinkRequest;
import by.zhukovsky.LinkShortener.dto.StatsResponse;
import by.zhukovsky.LinkShortener.entity.Link;
import by.zhukovsky.LinkShortener.repository.LinkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {
    @InjectMocks LinkService subj;
    @Mock LinkRepository linkRepository;
    @Mock EncodeService encoder;
    @Mock StatsService statsService;


    @BeforeEach
    void setUp(){
        Map<String, Integer> linkCounterMap = new HashMap<>();
        statsService.setLinkCounterMap(linkCounterMap);
    }

    @AfterEach
    void tearDown(){
        statsService.setLinkCounterMap(Collections.emptyMap());
    }

    @Test
    @DisplayName("Should create short link and return it")
    void shouldCreateShortLink(){
        //given
        String originalUrl = "original_link";
        String shortUrl = "short_link";

        LinkRequest linkRequest = new LinkRequest(originalUrl);

        var entity = new Link()
                .setOriginalLink(originalUrl)
                .setShortLink(shortUrl);

        when(linkRepository.existsByOriginalLink(originalUrl)).thenReturn(false);
        when(encoder.generateRandomUrl()).thenReturn(shortUrl);

        //when
        String actualValue = subj.createShortLink(linkRequest);

        //then
        verify(statsService).addToStatsMap(shortUrl);
        verify(linkRepository).save(entity);
        assertEquals(shortUrl, actualValue);
    }

    @Test
    @DisplayName("Should throw EntityExistsException when a short link " +
            "for the provided link already exists")
    void shouldThrowEntityExistsExceptionWhenShortcutForLinkAlreadyExists(){
        //given
        String originalUrl = "original_link";
        LinkRequest linkRequest = new LinkRequest(originalUrl);
        when(linkRepository.existsByOriginalLink(originalUrl)).thenReturn(true);

        //when & then
        assertThrows(EntityExistsException.class,() -> subj.createShortLink(linkRequest));
        verify(statsService, never()).addToStatsMap(any());
        verify(linkRepository, never()).save(any());
        assertTrue(statsService.getLinkCounterMap().isEmpty());
    }

    @Test
    @DisplayName("Should return original link and increment usage counter for this link")
    void shouldReturnOriginalUrlAndIncrementCounter(){
        //given
        String originalUrl = "original_link";
        String shortUrl = "short_link";
        Link entity = new Link(1L, originalUrl, shortUrl);
        int counter = 0;
        int expectedCounter = counter + 1;
        statsService.getLinkCounterMap().put(shortUrl,counter);
        when(linkRepository.findByShortLink(shortUrl)).thenReturn(Optional.of(entity));

        //when
        String actualValue = subj.getOriginalUrl(shortUrl);

        //then
        verify(statsService).incrementLinkUsageCounter(shortUrl);
        assertEquals(originalUrl, actualValue);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when provided short url doesn't exist")
    void shouldThrowEntityNotFoundExceptionWhenShortLinkDoesNotExist(){
        //given
        String shortUrl = "short_link";
        when(linkRepository.findByShortLink(shortUrl)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,() -> subj.getOriginalUrl(shortUrl));
    }

    @Test
    @DisplayName("Should return list of top-4 links")
    void shouldReturnStatsForTop4Links() {
        //given
        statsService.getLinkCounterMap().put("short_link1",5);
        statsService.getLinkCounterMap().put("short_link2",3);
        statsService.getLinkCounterMap().put("short_link3",4);
        statsService.getLinkCounterMap().put("short_link4",1);
        statsService.getLinkCounterMap().put("short_link5",0);
        var stats1 = new StatsResponse("short_link1","original_link1",1,5);
        var stats2 = new StatsResponse("short_link2","original_link2",3,3);
        var stats3 = new StatsResponse("short_link3","original_link3",2,4);
        var stats4 = new StatsResponse("short_link4","original_link4",4,1);
        var stats5 = new StatsResponse("short_link5","original_link5",5,0);
        List<StatsResponse> list = List.of(stats1,stats2,stats3,stats4,stats5);
        int page = 0;
        int count = 4;
        Pageable pageable = PageRequest.of(page, count);
        when(statsService.getTotalStats()).thenReturn(list);

        //when
        var results = subj.getPagedStats(pageable);

        //verify
        assertEquals(count, results.size());
        assertTrue(results.contains(stats1));
        assertTrue(results.contains(stats2));
        assertTrue(results.contains(stats3));
        assertTrue(results.contains(stats4));
        assertFalse(results.contains(stats5));
    }

    @Test
    @DisplayName("Should return empty list when page number and items count greater" +
            "than total link count")
    void shouldReturnEmptyListWhenPageNumberAndItemCounterGreaterThanTotalLinkCount() {
        //given
        statsService.getLinkCounterMap().put("short_link1",5);
        statsService.getLinkCounterMap().put("short_link2",3);
        statsService.getLinkCounterMap().put("short_link3",4);
        statsService.getLinkCounterMap().put("short_link4",1);
        statsService.getLinkCounterMap().put("short_link5",0);
        var stats1 = new StatsResponse("short_link1","original_link1",1,5);
        var stats2 = new StatsResponse("short_link2","original_link2",3,3);
        var stats3 = new StatsResponse("short_link3","original_link3",2,4);
        var stats4 = new StatsResponse("short_link4","original_link4",4,1);
        var stats5 = new StatsResponse("short_link5","original_link5",5,0);
        List<StatsResponse> list = List.of(stats1,stats2,stats3,stats4,stats5);
        int page = 99;
        int count = 100;
        Pageable pageable = PageRequest.of(page, count);
        when(statsService.getTotalStats()).thenReturn(list);

        //when
        var results = subj.getPagedStats(pageable);

        //verify
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return one link stats by it short url")
    void shouldReturnStatsForOneLink(){
        //given
        statsService.getLinkCounterMap().put("short_link1",5);
        statsService.getLinkCounterMap().put("short_link2",3);
        statsService.getLinkCounterMap().put("short_link3",4);
        var stats1 = new StatsResponse("short_link1","original_link1",1,5);
        var stats2 = new StatsResponse("short_link2","original_link2",3,3);
        var stats3 = new StatsResponse("short_link3","original_link3",2,4);
        List<StatsResponse> list = List.of(stats1,stats2,stats3);
        when(statsService.getTotalStats()).thenReturn(list);

        //when
        var result = subj.getLinkStats("short_link2");

        //then
        assertEquals(stats2, result);
    }

    @Test
    @DisplayName("Should return EntityNotFoundException" +
            " when trying to get stats for non-existing short url")
    void shouldThrowEntityNotFoundExceptionWhenTryingToFindStatsForNonExistingLink(){
        //given
        statsService.getLinkCounterMap().put("short_link1",5);
        statsService.getLinkCounterMap().put("short_link2",3);
        statsService.getLinkCounterMap().put("short_link3",4);
        var stats1 = new StatsResponse("short_link1","original_link1",1,5);
        var stats2 = new StatsResponse("short_link2","original_link2",3,3);
        var stats3 = new StatsResponse("short_link3","original_link3",2,4);
        List<StatsResponse> list = List.of(stats1,stats2,stats3);
        when(statsService.getTotalStats()).thenReturn(list);

        //when & then
        assertThrows(EntityNotFoundException.class,() -> subj.getLinkStats("non-existing link"));
    }
}