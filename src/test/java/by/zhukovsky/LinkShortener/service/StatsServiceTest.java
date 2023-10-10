package by.zhukovsky.LinkShortener.service;

import by.zhukovsky.LinkShortener.entity.Link;
import by.zhukovsky.LinkShortener.repository.LinkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {
    @InjectMocks StatsService subj;
    @Mock LinkRepository repository;

    @BeforeEach
    void setUp(){
        Map<String, Integer> linkCounterMap = new HashMap<>();
        subj.setLinkCounterMap(linkCounterMap);
    }

    @AfterEach
    void tearDown(){
        subj.setLinkCounterMap(Collections.emptyMap());
    }

    @Test
    void shouldIncrementLinkUsageCounter() {
        //given
        String shortUrl = "6tVaDaf";
        int count = 2;
        int expectedValue = count + 1;
        subj.getLinkCounterMap().put(shortUrl, count);

        //when
        subj.incrementLinkUsageCounter(shortUrl);
        int actualValue = subj.getLinkCounterMap().get(shortUrl);

        //then
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void shouldAddNewShortLinkToMap() {
        //given
        String shortUrl = "short_url";

        //when
        subj.addToStatsMap(shortUrl);

        //then
        assertTrue(subj.getLinkCounterMap().containsKey(shortUrl));
        assertEquals(0, subj.getLinkCounterMap().get(shortUrl));
        assertEquals(1, subj.getLinkCounterMap().size());
    }

    @Test
    void getTotalStats() {
        //given
        subj.getLinkCounterMap().put("short_link1", 5);
        subj.getLinkCounterMap().put("short_link2", 3);
        subj.getLinkCounterMap().put("short_link3", 4);
        var dummyLink = new Link(1L, "dummy_original_link", "dummy_short_link");
        when(repository.findByShortLink(any())).thenReturn(Optional.of(dummyLink));

        //when
        var results = subj.getTotalStats();

        //then
        assertEquals(3, results.size());

        assertEquals(1, results.get(0).getRank());
        assertEquals("/l/short_link1", results.get(0).getLink());

        assertEquals(2, results.get(1).getRank());
        assertEquals("/l/short_link3", results.get(1).getLink());

        assertEquals(3, results.get(2).getRank());
        assertEquals("/l/short_link2", results.get(2).getLink());
    }
}