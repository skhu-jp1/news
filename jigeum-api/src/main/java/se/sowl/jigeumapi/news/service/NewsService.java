package se.sowl.jigeumapi.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.sowl.jigeumapi.dto.BingSearchResponse;
import se.sowl.jigeumapi.gpt.GPTResponseParserService;
import se.sowl.jigeumapi.gpt.GPTService;
import se.sowl.jigeumdomain.news.domain.News;
import se.sowl.jigeumdomain.news.repository.NewsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {
    private final BingSearchService bingSearchService;
    private final GPTService gptService;
    private final GPTResponseParserService gptResponseParserService;
    private final NewsRepository newsRepository;
    private final NewsUtils newsUtils;

    public List<News> getYesterdayNews() {
        return newsRepository.findAll();
    }

    // 매일 23:55에 실행될 메서드
    public void saveYesterdayNews() {
        List<BingSearchResponse> searchResults = bingSearchService.getYesterdayNews();
        log.info("{} 개의 뉴스를 조회했습니다.", searchResults.size());
        List<News> processedNews = processSearchResults(searchResults);
        if (!processedNews.isEmpty()) {
            log.info("{} 개의 뉴스를 저장했습니다.", processedNews.size());
            newsRepository.saveAll(processedNews);
        } else {
            log.warn("처리된 뉴스가 없습니다.");
        }
    }

    private List<News> processSearchResults(List<BingSearchResponse> searchResults) {
        List<String> newsItems = getStrings(searchResults);
        List<String> summariesAndTags = gptService.summarizeAndTagNews(newsItems);
        // 요약 및 예측 결과 확인 로그 추가
        log.info("GPT 요약 및 예측 결과: {}", summariesAndTags);

        List<News> processedNews = new ArrayList<>();
        for (int i = 0; i < summariesAndTags.size(); i++) {
            String summaryAndTag = summariesAndTags.get(i);

            if (summaryAndTag != null && !summaryAndTag.isEmpty()) {
                BingSearchResponse response = searchResults.get(i);
                String[] parts = newsUtils.extractSummaryAndTag(summaryAndTag);
                if (!parts[0].equals("처리 실패")) {
                    processedNews.add(newsUtils.convertToNews(response, parts[0], parts[1]));


                }
            }
        }

        return processedNews;
    }

    private List<String> getStrings(List<BingSearchResponse> searchResults) {
        return convertToNewsItems(searchResults);
    }

    private List<String> convertToNewsItems(List<BingSearchResponse> searchResults) {
        return searchResults.stream()
                .map(newsUtils::formatNewsItem)
                .collect(Collectors.toList());
    }

    private List<News> createProcessedNewsList(List<BingSearchResponse> searchResults, List<String> parsedResults) {
        List<News> processedNews = new ArrayList<>();
        convertSummaryToNews(searchResults, parsedResults, processedNews);
        return processedNews;
    }

    private void convertSummaryToNews(List<BingSearchResponse> searchResults, List<String> parsedResults, List<News> processedNews) {
        for (int i = 0; i < searchResults.size(); i++) {
            BingSearchResponse response = searchResults.get(i);
            if (i >= parsedResults.size()) {
                log.warn("뉴스 요약 실패: {}", response.getTitle());
                continue;
            }
            String[] summaryAndTag = newsUtils.extractSummaryAndTag(parsedResults.get(i));
            processedNews.add(newsUtils.convertToNews(response, summaryAndTag[0], summaryAndTag[1]));
        }
    }
    public List<News> getNewsByPublishedAt(LocalDateTime start, LocalDateTime end) {
        return newsRepository.findByPublishedAtBetween(start, end);
    }


}
