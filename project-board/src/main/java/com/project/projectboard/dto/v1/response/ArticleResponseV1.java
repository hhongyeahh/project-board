package com.project.projectboard.dto.v1.response;

import com.project.projectboard.dto.v1.ArticleDtoV1;

import java.time.LocalDateTime;

public record ArticleResponseV1(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String nickname

) {
    public static ArticleResponseV1 of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleResponseV1(id, title, content, hashtag, createdAt, email, nickname);
    }

    public static ArticleResponseV1 from(ArticleDtoV1 dto) {
        String nickname = dto.userAccountDtoV1().nickname();
        if (nickname == null || nickname.isBlank()) { // 닉네임은 optional 이라 null 인 경우 userId 받아오기
            nickname = dto.userAccountDtoV1().userId();
        }
        return new ArticleResponseV1(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDtoV1().email(),
                nickname
        );

    }
}