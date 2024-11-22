package se.sowl.jigeumapi.gpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GPTService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api-key}")
    private String openAiApiKey;

    @Value("${openai.api-url}")
    private String apiUrl;

    public List<String> summarizeAndTagNews(List<String> newsItems) {
        //기존 20에서 10으로 더 쪼개서 보내야 정확성이 높아지는 것같아 줄임
        int batchSize = 10;
        List<String> results = new ArrayList<>();

        for (int i = 0; i < newsItems.size(); i += batchSize) {
            int end = Math.min(i + batchSize, newsItems.size());
            List<String> batch = newsItems.subList(i, end);
            String batchResult = processBatch(batch);
            results.addAll(parseBatchResult(batchResult));
        }

        return results;
    }

    private String processBatch(List<String> newsItems) {
        try {
            String result = callGptApi(newsItems);
            // 배치 결과 확인 로그 뉴스#번호/요약
            log.info("GPT 배치 처리 결과: {}", result);

            if (!result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            log.error("뉴스 배치 가공 실패: {}", e.getMessage());
        }
        return String.join("---", Collections.nCopies(newsItems.size(), "요약: 처리 실패\n행보 예상: 오류"));
    }

    private String callGptApi(List<String> newsItems) throws JsonProcessingException {
        HttpHeaders headers = generateHttpHeader();
        Map<String, Object> requestBody = generateRequestBody(newsItems);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        // 디버그 로그 추가
        log.debug("GPT API 응답: {}", response.getBody());

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.path("choices").get(0).path("message").path("content").asText().trim();
    }

    private Map<String, Object> generateRequestBody(List<String> newsItems) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo-0125");
        requestBody.put("messages", generateMessages(newsItems));
        requestBody.put("max_tokens", 2000);
        requestBody.put("temperature", 0.3);
        return requestBody;
    }

    private Object[] generateMessages(List<String> newsItems) {
        String systemMessage =  "각 뉴스를 정확하게 요약하고 예측해야 합니다."
                + "각 뉴스는 반드시 아래 형식으로 응답해야 합니다:\n"
                + "요약: [뉴스의 핵심 내용]\n"
                + "행보 예상: [예상되는 행보]\n"
                + "다음 뉴스는 ---로 구분합니다.";

        String userMessage = generateUserMessage(newsItems);
        return new Object[]{
            Map.of("role", "system", "content", systemMessage),
            Map.of("role", "user", "content", userMessage)
        };
    }

    private String generateUserMessage(List<String> newsItems) {
        StringBuilder prompt = new StringBuilder("각 뉴스의 제목과 본문을 보고 정확히 해당 뉴스에만 집중하여 요약하세요. 다음 지침을 따르세요:\n");
        prompt.append("1. 제목과 본문에 포함된 핵심 정보를 바탕으로 요약을 작성합니다.\n");
        prompt.append("2. 요약은 단순히 제목만 반복하지 않습니다. \n");
        prompt.append("3. 정치인이 포함된 뉴스는 정치적 행보를 분석하고, 없는 경우 뉴스에서 언급하는 상황의 결과나 영향을 예상합니다.\n");
        prompt.append("각 뉴스는 반드시 아래 형식을 따릅니다:\n");
        prompt.append("뉴스 #N\n요약: [뉴스의 제목과 본문을 바탕으로 작성한 요약, 3문장 이상]\n행보 예상: [예상되는 행보]\n---\n");

        //매칭 정확성을 위해 번호 추가
        for (int i = 0; i < newsItems.size(); i++) {
            prompt.append("뉴스 #").append(i + 1).append("\n") // 번호 추가
                    .append(newsItems.get(i)) // 뉴스 본문
                    .append("\n---\n"); // 뉴스 구분
        }
        return prompt.toString();
    }

    private List<String> parseBatchResult(String batchResult) {
        List<String> parsed = new ArrayList<>();
        String[] items = batchResult.split("---"); // ---로 구분된 응답을 나눔
        log.debug("배치 결과: {}", batchResult);

        for (String item : items) {
            String trimmed = item.trim();
            if (trimmed.startsWith("뉴스 #") && trimmed.contains("요약:") && trimmed.contains("행보 예상:")) {
                parsed.add(trimmed);
            } else {
                log.warn("형식에 맞지 않은 뉴스: {}", trimmed);
                parsed.add("요약: 처리 실패\n행보 예상: 오류");
            }
        }
        return parsed;
    }

    private boolean validFormat(String response) {
        log.debug("응답 유효성 확인: {}", response);
        return response.startsWith("뉴스 #") &&
                response.contains("요약:") &&
                response.contains("행보 예상:");
    }


    private HttpHeaders generateHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openAiApiKey);
        return headers;
    }
}
