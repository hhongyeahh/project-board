package com.project.projectboard.dto.v2;
import com.project.projectboard.domain.Article;
import com.project.projectboard.domain.ArticleComment;

import java.time.LocalDateTime;

public record ArticleCommentDtoV2(
        Long id,
        Long articleId,
        UserAccountDtoV2 userAccountDto,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleCommentDtoV2 of(Long id, Long articleId, UserAccountDtoV2 userAccountDtoV2, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleCommentDtoV2(id, articleId, userAccountDtoV2, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDtoV2 from(ArticleComment entity) {
        return new ArticleCommentDtoV2(
                entity.getId(),
                entity.getArticle().getId(),
                UserAccountDtoV2.from(entity.getUserAccount()),
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