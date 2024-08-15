package com.project.projectboard.dto.v1.response;

import com.project.projectboard.dto.v1.ArticleWithCommentsDtoV1;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponseV1(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname,
        Set<ArticleCommentResponseV1> articleCommentsResponse
) {

    public static ArticleWithCommentsResponseV1 of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname,  Set<ArticleCommentResponseV1> articleCommentsResponse) {
        return new ArticleWithCommentsResponseV1(id, title, content, hashtag, createdAt, email, nickname, articleCommentsResponse);

    }

    public static ArticleWithCommentsResponseV1 from(ArticleWithCommentsDtoV1 dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponseV1(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.articleCommentDtos().stream()
                .map(ArticleCommentResponseV1::from)
                .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}