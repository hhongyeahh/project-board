package com.project.projectboard.dto.v2.response;

import com.project.projectboard.dto.v2.ArticleWithCommentsDtoV2;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponseV2(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname,
        Set<ArticleCommentResponseV2> articleCommentsResponse
) {

    public static ArticleWithCommentsResponseV2 of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname, Set<ArticleCommentResponseV2> articleCommentsResponse) {
        return new ArticleWithCommentsResponseV2(id, title, content, hashtag, createdAt, email, nickname, articleCommentsResponse);

    }

    public static ArticleWithCommentsResponseV2 from(ArticleWithCommentsDtoV2 dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponseV2(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.articleCommentDtos().stream()
                .map(ArticleCommentResponseV2::from)
                .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}