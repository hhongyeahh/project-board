package com.project.projectboard.service;

import com.project.projectboard.domain.Article;
import com.project.projectboard.domain.UserAccount;
import com.project.projectboard.domain.type.SearchType;
import com.project.projectboard.dto.v1.ArticleDtoV1;
import com.project.projectboard.dto.v1.ArticleWithCommentsDtoV1;
import com.project.projectboard.dto.v1.UserAccountDtoV1;
import com.project.projectboard.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceV1Test {

    @InjectMocks private ArticleServiceV1 sut;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("[검색] 검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDtoV1> articles = sut.searchArticles(null, null, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("[검색] 검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDtoV1> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("[검색] 단건조회 - 게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleWithCommentsDtoV1 dto = sut.getArticle(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("[검색 - 예외] 게시글이 없으면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticle_thenThrowsException() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("[생성/저장] 게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // Given
        ArticleDtoV1 dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());
        // When
        sut.saveArticle(dto);
        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("[수정] 게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // Given
        Article article = createArticle();
        ArticleDtoV1 dto = createArticleDto("새 타이틀", "새 내용 #springboot");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);
        // When
        sut.updateArticle(dto);

        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());

    }

    @DisplayName("[삭제] 게시글의 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // Given
        Long articleId = 1L;
        given(articleRepository.getReferenceById(articleId)).willReturn(createArticle());
        willDoNothing().given(articleRepository).flush();

        // When
        sut.deleteArticle(1L);

        // Then
        then(articleRepository).should().getReferenceById(articleId);
        then(articleRepository).should().flush();
    }

    private Article createArticle() {
        return createArticle(1L);
    }

    private Article createArticle(Long id) {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "hashtag"
        );
    }

    private UserAccount createUserAccount() {
        return null;
    }

    private ArticleDtoV1 createArticleDto() {
        return createArticleDto("title", "content");
    }

    private ArticleDtoV1 createArticleDto(String title, String content) {
        return ArticleDtoV1.of(
                1L,
                createUserAccountDto(),
                title,
                content,
                null,
                LocalDateTime.now(),
                "user1",
                LocalDateTime.now(),
                "user1");
    }

    private UserAccountDtoV1 createUserAccountDto() {
        return UserAccountDtoV1.of(
                "user1",
                "password",
                "user1@mail.com",
                "user1",
                "This is memo",
                LocalDateTime.now(),
                "user1",
                LocalDateTime.now(),
                "user1"
        );
    }
}