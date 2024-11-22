package se.sowl.jigeumapi.gpt;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GPTResponseParserService {
    public List<String> parseBatchResult(String batchResult) {
        List<String> parsed = new ArrayList<>();
        String[] items = batchResult.split("---");
        for (String item : items) {
            String trimmed = item.trim();
            if (trimmed.startsWith("요약:") && trimmed.contains("행보 예상:")) {
                parsed.add(trimmed);
            } else {
                parsed.add("요약: 처리 실패\n행보 예상: 오류");
            }
        }
        return parsed;
    }
}
