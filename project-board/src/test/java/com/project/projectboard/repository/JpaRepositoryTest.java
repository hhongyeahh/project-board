package com.project.projectboard.repository;

import com.project.projectboard.config.JpaConfig;
import com.project.projectboard.domain.Article;
import com.project.projectboard.domain.UserAccount;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//CRUD test
@ActiveProfiles("testdb")//내가지정한 inmemorydb를 사용하겠다.
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//길어서 프로퍼티스에 전역설정함
@DisplayName("JPA 연결 테스트")//클래스 레벨로 Junit5기능을 활용하여 테스트 네임을 하나 넣어줌
@DataJpaTest // sliceTest할거라 DataJpaTest의 도움을 받음 -> 메서드 단위로 트랜젝션 걸려있음 Rollback으로 작동
    //그냥 이렇게 하면 Test는 JpaConfig의 존재를 모르기 때문에(내가 만든거니 그래서 import로 추가)
    //이걸 하지 않으면 Jpaconfig에서 넣어준 auditing기능이 동작하지 않음
@Import(JpaConfig.class)
//@ExtendWith()//autowired 기능에 대한 로직들이 들어가 있음 -> 생성자 주입 패턴으로 필드를 만들 수 있음
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                      @Autowired ArticleCommentRepository articleCommentRepository,
                      @Autowired UserAccountRepository userAccountRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository =userAccountRepository;
    }

    @DisplayName("select 테스트")
    @Test
    public void givenTestData_whenSelecting_thenWorksFine() throws Exception {

        //given

        //when
        List<Article> articles = articleRepository.findAll();

        //then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);

    }

    @DisplayName("insert 테스트")
    @Test
    public void givenTestData_whenInserting_thenWorksFine() throws Exception {

        //given
        long previousCount = articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("newUserId","password",null,null,null));
        Article article = Article.of(userAccount,"new article","new content","#newHashtag");
        //when
        articleRepository.save(article);
        //then
        assertThat(articleRepository.count()).isEqualTo(previousCount+1);

    }

    @DisplayName("update 테스트")
    @Test
    public void givenTestData_whenUpdating_thenWorksFine() throws Exception {

        //given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updateHashtag = "#springboot";
        article.setHashtag(updateHashtag);

        //when
        Article savedArticle = articleRepository.save(article);

        //then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag",updateHashtag);

    }

    @DisplayName("delete 테스트")
    @Test
    public void givenTestData_whenDeleting_thenWorksFine() throws Exception {

        //given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size(); //양방향 바인딩으로 인해 가져올 수 있음

        //when
        articleRepository.delete(article);

        //then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount-1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount-deletedCommentsSize);



    }
}