package by.zhukovsky.LinkShortener.repository;

import by.zhukovsky.LinkShortener.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByShortLink(String original);
    boolean existsByOriginalLink(String original);
}
