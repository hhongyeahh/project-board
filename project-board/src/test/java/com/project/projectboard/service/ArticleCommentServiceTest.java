package com.project.projectboard.service;

import com.project.projectboard.domain.Article;
import com.project.projectboard.domain.ArticleComment;
import com.project.projectboard.domain.UserAccount;
import com.project.projectboard.dto.v1.ArticleCommentDtoV1;
import com.project.projectboard.dto.v1.UserAccountDtoV1;
import com.project.projectboard.repository.ArticleCommentRepository;
import com.project.projectboard.repository.ArticleRepository;
import com.project.projectboard.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

    @DisplayName("비즈니스 로직 - 댓글")
    @ExtendWith(MockitoExtension.class)
    class ArticleCommentServiceTest {

        @InjectMocks private ArticleCommentService sut;

        @Mock private ArticleRepository articleRepository;
        @Mock private ArticleCommentRepository articleCommentRepository;
        @Mock private UserAccountRepository userAccountRepository;

        @DisplayName("[검색] 게시글 ID로 조회하면, 해당하는 댓글 리스트를 반환한다.")
        @Test
        void givenArticleId_whenSearchingArticleComments_thenReturnsArticleComments() {
            // Given
            Long articleId = 1L;
            ArticleComment expectedParentComment = createArticleComment(1L, "parent content");
            ArticleComment expectedChildComment = createArticleComment(2L, "child content");
            given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(
                    expectedParentComment,
                    expectedChildComment
            ));

            // When
            List<ArticleCommentDtoV1> actual = sut.searchArticleComments(articleId);

            // Then
            assertThat(actual).hasSize(2);
            assertThat(actual)
                    .extracting("id", "articleId", "parentCommentId", "content")
                    .containsExactlyInAnyOrder(
                            tuple(1L, 1L, null, "parent content"),
                            tuple(2L, 1L, 1L, "child content")
                    );
            then(articleCommentRepository).should().findByArticle_Id(articleId);
        }

        @DisplayName("[저장] 댓글 정보를 입력하면, 댓글을 저장한다.")
        @Test
        void givenArticleCommentInfo_whenSavingArticleComment_thenSavesArticleComment() {
            // Given
            ArticleCommentDtoV1 dto = createArticleCommentDto("댓글");
            given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
            given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
            given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

            // When
            sut.saveArticleComment(dto);

            // Then
            then(articleRepository).should().getReferenceById(dto.articleId());
            then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
            then(articleCommentRepository).should(never()).getReferenceById(anyLong());
            then(articleCommentRepository).should().save(any(ArticleComment.class));
        }

        @DisplayName("[저장 - 예외] 댓글 저장을 시도했는데 맞는 게시글이 없으면, 경고 로그를 찍고 아무것도 안 한다.")
        @Test
        void givenNonexistentArticle_whenSavingArticleComment_thenLogsSituationAndDoesNothing() {
            // Given
            ArticleCommentDtoV1 dto = createArticleCommentDto("댓글");
            given(articleRepository.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);

            // When
            sut.saveArticleComment(dto);

            // Then
            then(articleRepository).should().getReferenceById(dto.articleId());
            then(userAccountRepository).shouldHaveNoInteractions();
            then(articleCommentRepository).shouldHaveNoInteractions();
        }

//        @DisplayName("부모 댓글 ID와 댓글 정보를 입력하면, 대댓글을 저장한다.")
//        @Test
//        void givenParentCommentIdAndArticleCommentInfo_whenSaving_thenSavesChildComment() {
//            // Given
//            Long parentCommentId = 1L;
//            ArticleComment parent = createArticleComment(parentCommentId, "댓글");
//            ArticleCommentDtoV1 child = createArticleCommentDto(parentCommentId, "대댓글");
//            given(articleRepository.getReferenceById(child.articleId())).willReturn(createArticle());
//            given(userAccountRepository.getReferenceById(child.userAccountDto().userId())).willReturn(createUserAccount());
//
//            // When
//            sut.saveArticleComment(child);
//
//            // Then
//            then(articleRepository).should().getReferenceById(child.articleId());
//            then(userAccountRepository).should().getReferenceById(child.userAccountDto().userId());
//            then(articleCommentRepository).should(never()).save(any(ArticleComment.class));
//        }

        @DisplayName("[삭제] 댓글 ID를 입력하면, 댓글을 삭제한다.")
        @Test
        void givenArticleCommentId_whenDeletingArticleComment_thenDeletesArticleComment() {
            // Given
            Long articleCommentId = 1L;
            String userId = "uno";
            willDoNothing().given(articleCommentRepository).deleteByIdAndUserAccount_UserId(articleCommentId, userId);

            // When
            sut.deleteArticleComment(articleCommentId, userId);

            // Then
            then(articleCommentRepository).should().deleteByIdAndUserAccount_UserId(articleCommentId, userId);
        }


        private ArticleCommentDtoV1 createArticleCommentDto(String content) {
            return createArticleCommentDto(null, content);
        }

        private ArticleCommentDtoV1 createArticleCommentDto(Long parentCommentId, String content) {
            return createArticleCommentDto(1L, parentCommentId, content);
        }

        private ArticleCommentDtoV1 createArticleCommentDto(Long id, Long parentCommentId, String content) {
            return ArticleCommentDtoV1.of(
                    id,
                    1L,
                    createUserAccountDto(),
                    content,
                    LocalDateTime.now(),
                    "User1",
                    LocalDateTime.now(),
                    "User1"
            );
        }

        private UserAccountDtoV1 createUserAccountDto() {
            return UserAccountDtoV1.of(
                    "User1",
                    "password",
                    "User1@mail.com",
                    "User1",
                    "This is memo",
                    LocalDateTime.now(),
                    "User1",
                    LocalDateTime.now(),
                    "User1"
            );
        }

        private ArticleComment createArticleComment(Long id, String content) {
            ArticleComment articleComment = ArticleComment.of(
                    createArticle(),
                    createUserAccount(),
                    content
            );
            ReflectionTestUtils.setField(articleComment, "id", id);

            return articleComment;
        }

        private UserAccount createUserAccount() {
            return UserAccount.of(
                    "User1",
                    "password",
                    "User1@email.com",
                    "User1",
                    null
            );
        }

        private Article createArticle() {
            Article article = Article.of(
                    createUserAccount(),
                    "title",
                    "content",
                    "hashtag"
            );
            ReflectionTestUtils.setField(article, "id", 1L);
            return article;
        }
}