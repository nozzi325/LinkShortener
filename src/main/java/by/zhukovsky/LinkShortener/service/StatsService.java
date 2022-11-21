package by.zhukovsky.LinkShortener.service;

import by.zhukovsky.LinkShortener.dto.StatsResponse;
import by.zhukovsky.LinkShortener.repository.LinkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {
    private Map<String, Integer> linkCounterMap = new HashMap<>();
    private final LinkRepository repository;

    public Map<String, Integer> getLinkCounterMap() {
        return linkCounterMap;
    }
    public void setLinkCounterMap(Map<String, Integer> linkCounterMap) {
        this.linkCounterMap = linkCounterMap;
    }

    public StatsService(LinkRepository repository) {
        this.repository = repository;
    }

    protected void addToStatsMap(String shortUrl){
        linkCounterMap.put(shortUrl,0);
    }

    protected void incrementLinkUsageCounter(String shortUrl){
        synchronized (linkCounterMap){
            Integer count = linkCounterMap.get(shortUrl).intValue();
            linkCounterMap.compute(shortUrl, (k, v) -> count+1);
        }
    }

    protected List<StatsResponse> getTotalStats() {
        List<StatsResponse> statsResponses = new ArrayList<>(linkCounterMap.size());

        var entryList = linkCounterMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : entryList){
            String originalLink = repository.findByShortLink(entry.getKey())
                    .get()
                    .getOriginalLink();

            var entity = new StatsResponse()
                    .setOriginal(originalLink)
                    .setLink("/l/" + entry.getKey())
                    .setRank(rank)
                    .setCount(entry.getValue());

            statsResponses.add(entity);
            rank++;
        }

        return statsResponses;
    }

}
