package se.sowl.jigeumdomain.news.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sowl.jigeumdomain.news.domain.News;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    // 특정 날짜의 게시된 뉴스 데이터 조회
    List<News> findByPublishedAtBetween(LocalDateTime start, LocalDateTime end);
}
