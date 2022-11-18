package by.zhukovsky.LinkShortener.service;

import by.zhukovsky.LinkShortener.dto.LinkRequest;
import by.zhukovsky.LinkShortener.dto.StatsResponse;
import by.zhukovsky.LinkShortener.entity.Link;
import by.zhukovsky.LinkShortener.repository.LinkRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class LinkService {
    public static Map<String, Integer> linkCounterMap = new HashMap<>();
    private final EncodeService encoder;
    private final LinkRepository repository;

    public LinkService(EncodeService encoder, LinkRepository repository) {
        this.encoder = encoder;
        this.repository = repository;
    }

    public String createShortLink(LinkRequest request){
        String originalUrl = request.getOriginal();

        if (repository.existsByOriginalLink(originalUrl)){
            throw new EntityExistsException(String.format("Link for %s already exists", originalUrl));
        }

        String shortUrl = encoder.generateRandomUrl();

        var entity = new Link()
                .setOriginalLink(originalUrl)
                .setShortLink(shortUrl);

        linkCounterMap.put(shortUrl,0);
        repository.save(entity);

        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        var entity = repository.findByShortLink(shortUrl)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Link for \"%s\" not found",shortUrl)));
        incrementLinkUsageCounter(shortUrl);
        return entity.getOriginalLink();
    }

    public void incrementLinkUsageCounter(String shortUrl){
        synchronized (linkCounterMap){
            Integer count = linkCounterMap.get(shortUrl).intValue();
            linkCounterMap.compute(shortUrl, (k, v) -> count+1);
        }
    }

    public StatsResponse getLinkStats(String shortUrl){
        var entity = getTotalStats().stream()
                .filter(link -> link.getLink().contains(shortUrl))
                .findFirst();

        if (!entity.isPresent()){
            throw new EntityNotFoundException(String.format("Link for \"%s\" not found",shortUrl));
        }

        return entity.get();
    }

    public List<StatsResponse> getTotalStats() {
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
