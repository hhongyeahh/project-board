package com.project.projectboard.dto.v2.response;

import com.project.projectboard.dto.v2.ArticleCommentDtoV2;

import java.time.LocalDateTime;

public record ArticleCommentResponseV2(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname
) {

    public static ArticleCommentResponseV2 of(Long id, String content, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleCommentResponseV2(id, content, createdAt, email, nickname);
    }


    public static ArticleCommentResponseV2 from(ArticleCommentDtoV2 dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return ArticleCommentResponseV2.of(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname
        );
    }
}