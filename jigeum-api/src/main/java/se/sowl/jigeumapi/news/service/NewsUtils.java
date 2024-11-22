package se.sowl.jigeumapi.news.service;

import org.springframework.stereotype.Component;
import se.sowl.jigeumapi.dto.BingSearchResponse;
import se.sowl.jigeumdomain.news.domain.News;

@Component
public class NewsUtils {
    public String formatNewsItem(BingSearchResponse result) {
        return String.format("제목: %s\n내용: %s", result.getTitle(), result.getContent());
    }

    public News convertToNews(BingSearchResponse response, String summary, String prediction) {
        return News.builder()
            .title(cleanText(response.getTitle()))
            .newsUrl(response.getNewsUrl())
            .content(cleanText(response.getContent()))
            .aiContent(cleanAndTruncate(summary, 200))
            .publishedAt(response.getPublishedAt())
            .thumbnailUrl(response.getThumbnailUrl())
            .politicianPrediction(cleanAndTruncate(prediction, 2000))  // tag 필드에 행보 예상 저장
            .build();
    }

    public String[] extractSummaryAndTag(String parsedResult) {
        try {
            String[] parts = parsedResult.split("행보 예상:");
            if (parts.length != 2) {
                return new String[]{"처리 실패", "처리 실패"};
            }

            String summaryPart = parts[0];
            int summaryStart = summaryPart.indexOf("요약:") + "요약:".length();
            String summary = summaryPart.substring(summaryStart).trim();
            String prediction = parts[1].trim();

            // 빈 결과 체크
            if (summary.isEmpty() || prediction.isEmpty()) {
                return new String[]{"처리 실패", "처리 실패"};
            }

            return new String[]{summary, prediction};
        } catch (Exception e) {
            return new String[]{"처리 실패", "처리 실패"};
        }
    }

    private String cleanText(String text) {
        if (text == null) return "";
        return text.replaceAll("[#-]", "").trim();
    }

    private String cleanAndTruncate(String text, int maxLength) {
        if (text == null || text.isEmpty()) return "정보 없음";
        String cleaned = cleanText(text);
        return cleaned.length() > maxLength ? cleaned.substring(0, maxLength) : cleaned;
    }
}
