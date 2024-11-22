package se.sowl.jigeumapi.news.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.sowl.jigeumapi.news.service.NewsService;
import se.sowl.jigeumdomain.news.domain.News;

import java.util.List;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsViewController {
    private final NewsService newsService;

    @GetMapping("/yesterday")
    public String getYesterdayNews(Model model) {
        List<News> newsList = newsService.getYesterdayNews();
        model.addAttribute("newsList", newsList);
        return "news/yesterday";
    }
}
