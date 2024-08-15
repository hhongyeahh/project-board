package com.project.projectboard.dto.v2;

import com.project.projectboard.domain.Article;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsDtoV2(

            Long id,
            UserAccountDtoV2 userAccountDto,
            Set<ArticleCommentDtoV2> articleCommentDtos,
            String title,
            String content,
            String hashtag,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy
    ) {

    public static ArticleWithCommentsDtoV2 of(Long id,
                                              UserAccountDtoV2 userAccountDtoV2,
                                              Set<ArticleCommentDtoV2> articleCommentDtoV2s,
                                              String title,
                                              String content,
                                              String hashtag,
                                              LocalDateTime createdAt,
                                              String createdBy,
                                              LocalDateTime modifiedAt,
                                              String modifiedBy) {
        return new ArticleWithCommentsDtoV2(id, userAccountDtoV2, articleCommentDtoV2s, title, content, hashtag,createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleWithCommentsDtoV2 from(Article entity) {
        return new ArticleWithCommentsDtoV2(
                entity.getId(),
                UserAccountDtoV2.from(entity.getUserAccount()),
                entity.getArticleComments().stream()
                        .map(ArticleCommentDtoV2::from)
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