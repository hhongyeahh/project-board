package com.project.projectboard.dto.v1;

import com.project.projectboard.domain.Article;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public record ArticleWithCommentsDtoV1 (

            Long id,
            UserAccountDtoV1 userAccountDto,
            Set<ArticleCommentDtoV1> articleCommentDtos,
            String title,
            String content,
            String hashtag,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy
    ) {

    public static ArticleWithCommentsDtoV1 of(Long id,
                                              UserAccountDtoV1 userAccountDtoV1,
                                              Set<ArticleCommentDtoV1> articleCommentDtoV1s,
                                              String title,
                                              String content,
                                              String hashtag,
                                              LocalDateTime createdAt,
                                              String createdBy,
                                              LocalDateTime modifiedAt,
                                              String modifiedBy) {
        return new ArticleWithCommentsDtoV1(id, userAccountDtoV1, articleCommentDtoV1s, title, content, hashtag,createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleWithCommentsDtoV1 from(Article entity) {
        return new ArticleWithCommentsDtoV1(
                entity.getId(),
                UserAccountDtoV1.from(entity.getUserAccount()),
                entity.getArticleComments().stream()
                        .map(ArticleCommentDtoV1::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }
}