package se.sowl.jigeumapi.news.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sowl.jigeumapi.news.service.NewsService;

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
}
