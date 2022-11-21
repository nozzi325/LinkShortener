package by.zhukovsky.LinkShortener.service;

import by.zhukovsky.LinkShortener.dto.LinkRequest;
import by.zhukovsky.LinkShortener.dto.StatsResponse;
import by.zhukovsky.LinkShortener.entity.Link;
import by.zhukovsky.LinkShortener.repository.LinkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class LinkService {
    private final EncodeService encoder;

    private final StatsService statsService;
    private final LinkRepository repository;

    public LinkService(EncodeService encoder, LinkRepository repository, StatsService statsService) {
        this.encoder = encoder;
        this.repository = repository;
        this.statsService = statsService;
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

        statsService.addToStatsMap(shortUrl);
        repository.save(entity);

        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        var entity = repository.findByShortLink(shortUrl)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Link for \"%s\" not found",shortUrl)));
        statsService.incrementLinkUsageCounter(shortUrl);
        return entity.getOriginalLink();
    }

    public StatsResponse getLinkStats(String shortUrl){
        var entity =  statsService.getTotalStats().stream()
                .filter(link -> link.getLink().contains(shortUrl))
                .findFirst();

        if (!entity.isPresent()){
            throw new EntityNotFoundException(String.format("Link for \"%s\" not found",shortUrl));
        }

        return entity.get();
    }

    public List<StatsResponse> getPagedStats(Pageable pageable){
        var totalStats = statsService.getTotalStats();

        int start = Math.min((int) pageable.getOffset(),totalStats.size());
        int end = Math.min(start + pageable.getPageSize(), totalStats.size());

        Page<StatsResponse> page = new PageImpl<>(
                totalStats.subList(start, end),
                pageable,
                totalStats.size()
        );

        return page.getContent();
    }
}
