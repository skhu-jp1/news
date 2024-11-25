package se.sowl.jigeumapi.news.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.sowl.jigeumapi.news.service.NewsService;
import se.sowl.jigeumdomain.news.domain.News;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsViewController {
    private final NewsService newsService;

    /**
     * "어제" 뉴스 가져오기
     */
    @GetMapping("/yesterday")
    public String getYesterdayNews(Model model) {
        // 어제 날짜 계산
        LocalDate yesterday = LocalDate.now().minusDays(1); // 전날 계산
        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.plusDays(1).atStartOfDay();

        List<News> newsList = newsService.getNewsByPublishedAt(start, end);

        model.addAttribute("newsList", newsList);
        model.addAttribute("selectedDate", yesterday.toString());

        return "news/yesterday";
    }

    /**
     * 특정 날짜의 뉴스 가져오기
     */
    @GetMapping("/{date}")
    public String getNewsByDate(@PathVariable String date, Model model) {
        LocalDate selectedDate = LocalDate.parse(date); // 날짜 그대로 사용
        LocalDateTime start = selectedDate.atStartOfDay();
        LocalDateTime end = selectedDate.plusDays(1).atStartOfDay();

        List<News> newsList = newsService.getNewsByPublishedAt(start, end);

        model.addAttribute("newsList", newsList);
        model.addAttribute("selectedDate", selectedDate.toString());

        return "news/yesterday"; // 기존 템플릿 사용
    }

}
