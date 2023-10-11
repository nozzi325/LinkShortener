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

    public Link(Long id, String originalLink, String shortLink, Integer count) {
        this.id = id;
        this.originalLink = originalLink;
        this.shortLink = shortLink;
        this.count = count;
    }

    public void incrementCounter() {
        count++;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
