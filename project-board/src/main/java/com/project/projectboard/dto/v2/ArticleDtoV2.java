package com.project.projectboard.dto.v2;

import com.project.projectboard.domain.Article;

import java.time.LocalDateTime;

public record ArticleDtoV2(
        Long id,
        UserAccountDtoV2 userAccountDtoV2,
        String hashtag,
        String title,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
      ) {

    public static ArticleDtoV2 of(UserAccountDtoV2 userAccountDtoV2, String title, String content, String hashtag) {
        return new ArticleDtoV2(null, userAccountDtoV2, title, content, hashtag, null, null, null, null);
    }

    public static ArticleDtoV2 of(Long id, UserAccountDtoV2 userAccountDtoV2, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDtoV2(id, userAccountDtoV2, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleDtoV2 from(Article entity) {
        //Article 은 Dto 의 존재를 모르고, 오로지 Dto 만 연관관계 매핑을 위해 Article 의 존재를 알고 있음
        //Article의 변화는 Dto에 영향을 주지만, Dto의 변화는 가장 중요한 도메인 코드(Article)에 영향을 주지 않음
        return new ArticleDtoV2(
                entity.getId(),
                UserAccountDtoV2.from(entity.getUserAccount()),
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
                userAccountDtoV2.toEntity(),
                title,
                content,
                hashtag

        );
    }

}