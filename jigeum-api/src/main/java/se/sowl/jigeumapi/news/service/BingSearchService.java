package se.sowl.jigeumapi.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.sowl.jigeumapi.dto.BingSearchResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BingSearchService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${azure.bing-search.api-key}")
    private String bingSearchApiKey;

    public List<BingSearchResponse> getYesterdayNews() {
        HttpEntity<String> header = generateHttpHeader();
        String searchUrl = "https://api.bing.microsoft.com/v7.0/news/search?q=정치&category=Politics&sortBy=Relevance&count=30&setLang=ko&mkt=ko-KR";
        List<Map<String, Object>> newsItems = search(searchUrl, header);

        // Bing API 응답 데이터 로깅
        log.info("Bing API 응답 뉴스 개수: {}", newsItems.size());
        for (Map<String, Object> newsItem : newsItems) {
            log.info("Bing 뉴스: {}", newsItem);
        }

        return newsItems.stream()
            .map(BingSearchResponse::new)
            .filter(this::hasThumbnail)
            .collect(Collectors.toList());
    }


    private List<Map<String, Object>> search(String searchUrl, HttpEntity<String> header) {
        ResponseEntity<Map> response = restTemplate.exchange(searchUrl, HttpMethod.GET, header, Map.class);
        return (List<Map<String, Object>>) response.getBody().get("value");
    }

    private HttpEntity<String> generateHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", bingSearchApiKey);
        return new HttpEntity<>(headers);
    }

    private boolean hasThumbnail(BingSearchResponse response) {
        return response.getThumbnailUrl() != null && !response.getThumbnailUrl().isEmpty();
    }
}
