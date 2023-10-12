package by.zhukovsky.LinkShortener.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity
public class Link {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_link", unique = true)
    private String originalLink;

    @Column(name = "short_link", unique = true)
    private String shortLink;

    private Integer count = 0;

    public Link() {
    }

    public Link(String originalLink, String shortLink) {
        this.originalLink = originalLink;
        this.shortLink = shortLink;
    }

    public Link(Long id, String originalLink, String shortLink) {
        this.id = id;
        this.originalLink = originalLink;
        this.shortLink = shortLink;
    }

    public void incrementCounter() {
        count++;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public Integer getCount() {
        return count;
    }
}
