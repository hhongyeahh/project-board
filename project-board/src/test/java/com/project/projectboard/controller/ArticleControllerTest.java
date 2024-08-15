package com.project.projectboard.controller;

import com.project.projectboard.config.SecurityConfig;
import com.project.projectboard.dto.v1.ArticleWithCommentsDtoV1;
import com.project.projectboard.dto.v1.UserAccountDtoV1;
import com.project.projectboard.service.ArticleServiceV1;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@DisplayName("View 컨트롤러 - 게시글")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)//test 대상이 되는 컨트롤러만 빈으로 읽어오기
public class ArticleControllerTest {
    private final MockMvc mvc;

    @MockBean
    private ArticleServiceV1 articleService;//MockBean 에 대해서 생성자 주입이 구현되어있지 않아서, 필드 주입만 가능

    public ArticleControllerTest(@Autowired MockMvc mvc) {//실제 소스코드에는 @Autowired 생략가능하지만, Test에서는 불가
        this.mvc = mvc;
    }


    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnArticlesView() throws Exception {

        //given
        //검색어 없는 경우
        given(articleService.searchArticles(null, eq(null), any(Pageable.class))).willReturn(Page.empty());

        //when & then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"));
        then(articleService).should().searchArticles(null, eq(null), any(Pageable.class));
        //서버에서 게시글 목록을 내려줌 -> view에 모델애트리부트로 데이터를 밀어넣어줬다는 뜻
        //모델 애트리뷰트라는 맵에 해당 이름(articles)의 키가 있는지 체크 (내용 까지 검증하는 것은 아님)
    }

    @DisplayName("[view][GET] 게시글 단건(상세) 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnArticleView() throws Exception {

        //given
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDtoV1());

        //when & then
        mvc.perform(get("/articles/"+articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));
        then(articleService).should().getArticle(articleId);

    }


    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleSearchView_thenReturnArticleSearchView() throws Exception {

        //given

        //when & then
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search"))
        ;

    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnArticleHashtagSearchView() throws Exception {

        //given

        //when & then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("articles/search-hashtag"));
    }

    private ArticleWithCommentsDtoV1 createArticleWithCommentsDtoV1() {
        return ArticleWithCommentsDtoV1.of(
                1L,
                createUserAccountDtoV1(),
                Set.of(),
                "title",
                "content",
                "#hashtag",
                LocalDateTime.now(),
                "user1",
                LocalDateTime.now(),
                "user1"
        );
    }

    private UserAccountDtoV1 createUserAccountDtoV1() {
        return new UserAccountDtoV1(
                "user1",
                "pw",
                "user1@mail.com",
                "User1",
                "memo",
                LocalDateTime.now(),
                "user1",
                LocalDateTime.now(),
                "user1"
        );
    }

}
