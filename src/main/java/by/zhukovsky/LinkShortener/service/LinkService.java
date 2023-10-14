package by.zhukovsky.LinkShortener.service;

import by.zhukovsky.LinkShortener.dto.LinkRequest;
import by.zhukovsky.LinkShortener.dto.StatsResponse;
import by.zhukovsky.LinkShortener.entity.Link;
import by.zhukovsky.LinkShortener.repository.LinkRepository;
import by.zhukovsky.LinkShortener.utils.LinkValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LinkService {
    private final EncodeService encoder;
    private final LinkRepository repository;

    public LinkService(EncodeService encoder, LinkRepository repository) {
        this.encoder = encoder;
        this.repository = repository;
    }

    public String createShortLink(LinkRequest request) {
        String url = request.original();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        if (!LinkValidator.isValidLink(url)) {
            throw new IllegalArgumentException("Invalid original link: " +  request.original());
        }

        if (repository.existsByOriginalLink(url)) {
            throw new EntityExistsException("Short link for '" + url +"' already exists");
        }

        String shortUrl = encoder.generateRandomUrl();
        Link createdLink = new Link(url, shortUrl);
        repository.save(createdLink);

        return "/l/" + shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        Link link = findLinkByShortUrl(shortUrl);
        link.incrementCounter();
        repository.save(link);
        return link.getOriginalLink();
    }

    public StatsResponse getLinkStats(String shortUrl) {
        Link link = findLinkByShortUrl(shortUrl);
        int linkRank = repository.getRankByCount(link.getCount()) + 1;
        return new StatsResponse(
                "/l/" + link.getShortLink(),
                link.getOriginalLink(),
                linkRank,
                link.getCount());
    }

    public List<StatsResponse> getTotalStats(Pageable pageable) {
        Page<Link> page = repository.findAllByOrderByCountDesc(pageable);
        return page.getContent()
                .stream()
                .map(link -> new StatsResponse(
                        link.getOriginalLink(),
                        "/l/" + link.getShortLink(),
                        (page.getNumber() * page.getSize()) + page.getContent().indexOf(link) + 1,
                        link.getCount())
                )
                .collect(Collectors.toList());
    }

    private Link findLinkByShortUrl(String shortUrl) {
        return repository.findByShortLink(shortUrl)
                .orElseThrow(() -> new EntityNotFoundException("Requested resource '" + shortUrl +"' not found"));
    }
}
