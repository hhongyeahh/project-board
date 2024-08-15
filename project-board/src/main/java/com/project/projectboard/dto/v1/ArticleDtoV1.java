package com.project.projectboard.dto.v1;

import com.project.projectboard.domain.Article;

import java.time.LocalDateTime;

public record ArticleDtoV1(
        Long id,
        UserAccountDtoV1 userAccountDtoV1,
        String hashtag,
        String title,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
      ) {

    public static ArticleDtoV1 of(UserAccountDtoV1 userAccountDtoV1, String title, String content, String hashtag) {
        return new ArticleDtoV1(null, userAccountDtoV1, title, content, hashtag, null, null, null, null);
    }

    public static ArticleDtoV1 of(Long id, UserAccountDtoV1 userAccountDtoV1, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDtoV1(id, userAccountDtoV1, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleDtoV1 from(Article entity) {
        //Article 은 Dto 의 존재를 모르고, 오로지 Dto 만 연관관계 매핑을 위해 Article 의 존재를 알고 있음
        //Article의 변화는 Dto에 영향을 주지만, Dto의 변화는 가장 중요한 도메인 코드(Article)에 영향을 주지 않음
        return new ArticleDtoV1(
                entity.getId(),
                UserAccountDtoV1.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Article toEntity() {
        return Article.of(
                userAccountDtoV1.toEntity(),
                title,
                content,
                hashtag

        );
    }

}