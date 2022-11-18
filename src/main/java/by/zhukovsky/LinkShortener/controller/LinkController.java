package by.zhukovsky.LinkShortener.controller;

import by.zhukovsky.LinkShortener.dto.LinkRequest;
import by.zhukovsky.LinkShortener.dto.ShortLinkResponse;
import by.zhukovsky.LinkShortener.dto.StatsResponse;
import by.zhukovsky.LinkShortener.service.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LinkController {
    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("generate")
    public ShortLinkResponse generateShortLink(@RequestBody LinkRequest request){
        String shortLink = linkService.createShortLink(request);
        return new ShortLinkResponse("/l/" + shortLink);
    }

    @GetMapping("l/{short}")
    public ResponseEntity<Void> getAndRedirect(@PathVariable("short") String shortUrl){
        var url = linkService.getOriginalUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @GetMapping("stats/{short}")
    public StatsResponse getLinkStats(@PathVariable("short") String shortUrl){
        return linkService.getLinkStats(shortUrl);
    }

    @GetMapping("stats")
    public List<StatsResponse> getAllStats(){
        return linkService.getTotalStats();
    }
}
