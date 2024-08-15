package com.project.projectboard.dto.v1.response;

import com.project.projectboard.dto.v1.ArticleCommentDtoV1;

import java.time.LocalDateTime;

public record ArticleCommentResponseV1(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname
) {

    public static ArticleCommentResponseV1 of(Long id, String content, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleCommentResponseV1(id, content, createdAt, email, nickname);
    }


    public static ArticleCommentResponseV1 from(ArticleCommentDtoV1 dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return ArticleCommentResponseV1.of(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname
        );
    }
}