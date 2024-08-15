package com.project.projectboard.dto.v1;
import com.project.projectboard.domain.Article;
import com.project.projectboard.domain.ArticleComment;

import java.time.LocalDateTime;

public record ArticleCommentDtoV1(
        Long id,
        Long articleId,
        UserAccountDtoV1 userAccountDto,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleCommentDtoV1 of(Long id, Long articleId, UserAccountDtoV1 userAccountDtoV1, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleCommentDtoV1(id, articleId, userAccountDtoV1, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDtoV1 from(ArticleComment entity) {
        return new ArticleCommentDtoV1(
                entity.getId(),
                entity.getArticle().getId(),
                UserAccountDtoV1.from(entity.getUserAccount()),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public ArticleComment toEntity(Article entity) {
        return ArticleComment.of(
                entity,
                userAccountDto.toEntity(),
                content
        );
    }

}