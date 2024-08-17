package com.project.projectboard.controller;

import com.project.projectboard.config.SecurityConfig;
import com.project.projectboard.domain.constant.SearchType;
import com.project.projectboard.dto.ArticleWithCommentsDto;
import com.project.projectboard.dto.UserAccountDto;
import com.project.projectboard.service.ArticleService;
import com.project.projectboard.service.PaginationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
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
    private ArticleService articleService;//MockBean 에 대해서 생성자 주입이 구현되어있지 않아서, 필드 주입만 가능
    @MockBean
    private PaginationService paginationService;

    public ArticleControllerTest(@Autowired MockMvc mvc) {//실제 소스코드에는 @Autowired 생략가능하지만, Test에서는 불가
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnArticlesView() throws Exception {

        //given
        //검색어 없는 경우
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4)); // 페이지네이션

        //when & then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationBarNumbers"));
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
        //서버에서 게시글 목록을 내려줌 -> view에 모델애트리부트로 데이터를 밀어넣어줬다는 뜻
        //모델 애트리뷰트라는 맵에 해당 이름(articles)의 키가 있는지 체크 (내용 까지 검증하는 것은 아님)
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 감색어와 함께 호출")
    @Test
    public void givenSearchingKeyword_whenSearchingArticleView_thenReturnArticleView() throws Exception {

        //given
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        given(articleService.searchArticles(eq(searchType), eq(searchValue), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of()); // 페이지네이션

        //when & then
        mvc.perform(get("/articles")
                        .queryParam("searchType",searchType.name())
                        .queryParam("searchValue",searchValue)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("searchTypes"));
        then(articleService).should().searchArticles(eq(searchType), eq(searchValue), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());

    }
    @DisplayName("[view][GET] 게시글 단건(상세) 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnArticleView() throws Exception {

        //given
        Long articleId = 1L;
        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDtoV1());

        //when & then
        mvc.perform(get("/articles/"+articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));
        then(articleService).should().getArticleWithComments(articleId);

    }


    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    public void givenPagingAndSortingParams_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {

        //given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber,pageSize,Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1,2,3,4,5);
        given(articleService.searchArticles(null,null,pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(),Page.empty().getTotalPages())).willReturn(barNumbers);

        //When & Then
        mvc.perform(
                get("/articles")
                        .queryParam("page",String.valueOf(pageNumber))
                        .queryParam("size",String.valueOf(pageSize))
                        .queryParam("sort",sortName+","+direction)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBarNumbers",barNumbers));
        then(articleService).should().searchArticles(null,null,pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(),Page.empty().getTotalPages());
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

    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleSearchHashtagView_thenReturnArticleSearchHashtagView() throws Exception {

        //given
        List<String> hashtags = List.of("#java","#Spring","#Boot");
        given(articleService.searchArticlesViaHashtag(eq(null),any(Pageable.class))).willReturn(Page.empty());
        given(articleService.getHashtags()).willReturn(hashtags);
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1,2,3,4,5));

        //when & then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles",Page.empty()))
                .andExpect(model().attribute("hashtags",hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType",SearchType.HASHTAG))
                ;
        then(articleService).should().searchArticlesViaHashtag(eq(null),any(Pageable.class));
        then(articleService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(),anyInt());
    }

    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출, 해시태그 입력")
    @Test
    public void givenHashtag_whenRequestingArticleSearchHashtagView_thenReturnArticleSearchHashtagView() throws Exception {
        //given
        String hashtag = "#java";
        List<String> hashtags = List.of("#java","#Spring","#Boot");

        given(articleService.searchArticlesViaHashtag(eq(hashtag),any(Pageable.class))).willReturn(Page.empty());
        given(articleService.getHashtags()).willReturn(hashtags);
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1,2,3,4,5));

        //when & then
        mvc.perform(get("/articles/search-hashtag")
                        .queryParam("searchValue",hashtag)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles",Page.empty()))
                .andExpect(model().attribute("hashtags",hashtags))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType",SearchType.HASHTAG))

        ;
        then(articleService).should().searchArticlesViaHashtag(eq(hashtag),any(Pageable.class));
        then(articleService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(),anyInt());
    }

    private ArticleWithCommentsDto createArticleWithCommentsDtoV1() {
        return ArticleWithCommentsDto.of(
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

    private UserAccountDto createUserAccountDtoV1() {
        return new UserAccountDto(
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
