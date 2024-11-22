package se.sowl.jigeumdomain.news.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.sowl.jigeumdomain.news.domain.News;

public interface NewsRepository extends JpaRepository<News, Long> {

}
