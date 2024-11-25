package se.sowl.jigeumdomain.news.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String title;

    @Column(length = 5000)
    private String content;

    @Column(length = 5000, name = "ai_content")
    private String aiContent;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "news_url", length = 2000)
    private String newsUrl;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    private String politicianPrediction;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public News(String title, String content, String aiContent, String thumbnailUrl, String newsUrl, LocalDateTime publishedAt, String politicianPrediction) {
        this.title = title;
        this.content = content;
        this.aiContent = aiContent;
        this.thumbnailUrl = thumbnailUrl;
        this.newsUrl = newsUrl;
        this.publishedAt = publishedAt;
        this.politicianPrediction = politicianPrediction;
    }
}
