package se.sowl.jigeumapi.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    private final NewsService newsService;

    // 매일 23:55 실행
    @Scheduled(cron = "0 55 23 * * ?")
    public void scheduleNewsFetch() {
        log.info("스케줄링: 23:55에 뉴스 저장 시작");
        newsService.saveYesterdayNews(); // Bing API 호출 및 데이터 저장
    }
}
