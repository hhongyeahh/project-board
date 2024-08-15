package com.project.projectboard.dto.v2.response;

import com.project.projectboard.dto.v2.ArticleDtoV2;

import java.time.LocalDateTime;

public record ArticleResponseV2(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname

) {
    public static ArticleResponseV2 of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleResponseV2(id, title, content, hashtag, createdAt, email, nickname);
    }

    public static ArticleResponseV2 from(ArticleDtoV2 dto) {
        String nickname = dto.userAccountDtoV2().nickname();
        if (nickname == null || nickname.isBlank()) { // 닉네임은 optional 이라 null 인 경우 userId 받아오기
            nickname = dto.userAccountDtoV2().userId();
        }
        return new ArticleResponseV2(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDtoV2().email(),
                nickname
        );

    }
}