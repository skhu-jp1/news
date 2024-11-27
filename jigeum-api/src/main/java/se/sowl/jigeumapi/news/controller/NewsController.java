package se.sowl.jigeumapi.news.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.sowl.jigeumapi.news.service.NewsService;
import se.sowl.jigeumdomain.news.domain.News;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @PostMapping("/yesterday")
    public ResponseEntity<String> createNews() {
        newsService.saveYesterdayNews();
        return ResponseEntity.ok("OK");
    }

    // 특정 날짜의 뉴스 조회
    @GetMapping("/{date}")
    public List<News> getNewsByDate(@PathVariable String date) {
        LocalDateTime start = LocalDate.parse(date).atStartOfDay(); // 날짜의 00:00
        LocalDateTime end = start.plusDays(1).minusSeconds(1); // 날짜의 23:59:59
        return newsService.getNewsByPublishedAt(start, end); // 정확한 범위 전달
    }
}
