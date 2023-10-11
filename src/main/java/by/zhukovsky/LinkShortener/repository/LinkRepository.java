package by.zhukovsky.LinkShortener.repository;

import by.zhukovsky.LinkShortener.entity.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByShortLink(String shortLink);
    boolean existsByOriginalLink(String original);
    Page<Link> findAllByOrderByCountDesc(Pageable pageable);
    @Query(value = "SELECT count(*) FROM Link l WHERE l.count > :count", nativeQuery = true)
    int getRankByCount(int count);
}
