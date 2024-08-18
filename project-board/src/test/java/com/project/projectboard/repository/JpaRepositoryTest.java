package com.project.projectboard.repository;

import com.project.projectboard.config.JpaConfig;
import com.project.projectboard.domain.Article;
import com.project.projectboard.domain.UserAccount;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("JPA 연결 테스트")
@Import(JpaRepositoryTest.TestJpaConfig.class) // 아래의 어노테이션이 인식을 못하므로 수동 Import
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    // 추가된 사항
    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository,
                             @Autowired UserAccountRepository userAccountRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // Given

        // When
        List<Article> articles = articleRepository.findAll();

        // Then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);

    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        // Given
        long previousCount = articleRepository.count(); // 현재 repository의 aritcle 개수 카운트
        // 추가됨
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("mrcocoball", "pw", null, null, null));
        Article article = Article.of(userAccount, "new article", "new content", "#spring");

        // When
        // Article savedArticle = articleRepository.save(Article.of("new article", "new content", "#spring"));
        articleRepository.save(article);
        // repository에 새 article 추가

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);

    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        // 현재 repository 내부에서 id가 1l인 article 불러오기, 없으면 exception throw
        String updatedHashTag = "#springboot";
        // 업데이트할 해시태그
        article.setHashtag(updatedHashTag);
        // 해시태그 수정

        // When
        Article savedArticle = articleRepository.saveAndFlush(article);
        // save로만 할 경우 update 쿼리가 관측이 안됨

        // Then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashTag);

    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        // 현재 repository 내부에서 id가 1l인 article 불러오기, 없으면 exception throw
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        // 연관관계가 지정되어 있어 article 삭제 시 articlecomment도 같이 지워지므로 둘 다 개수 카운트
        int deletedCommentsSize = article.getArticleComments().size();

        // When
        articleRepository.delete(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);

    }

    @EnableJpaAuditing
    @TestConfiguration // 테스트할 때만 등록하라는 명령
    public static class TestJpaConfig{
        //security 랑 완전 분리되게끔 auditing-aware 에서 문제가 된 부분을 해당 Test 에서만 제외
        @Bean
        public AuditorAware<String> auditorAware(){
            return () -> Optional.of("uno");
        }
    }
}