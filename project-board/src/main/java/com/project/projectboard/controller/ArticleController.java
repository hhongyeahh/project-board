package com.project.projectboard.controller;

import com.project.projectboard.domain.type.SearchType;
import com.project.projectboard.dto.v1.response.ArticleResponseV1;
import com.project.projectboard.dto.v1.response.ArticleWithCommentsResponseV1;
import com.project.projectboard.service.ArticleServiceV1;
import com.project.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/articles")
public class ArticleController {
    /*
     * /articles
     * /articles/{article-id}
     * /articles/search
     * /articles/search-hashtag
     */

    private final ArticleServiceV1 articleService;
    private final PaginationService paginationService;


    @GetMapping//게시글 리스트 페이지 - 정상 호출
    public String articles(
            @RequestParam(required=false)SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size=10, sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ){
        //        map.addAttribute("articles", articleService.searchArticles(searchType,searchValue, pageable).map(ArticleResponseV1::from));
        //페이지네이션 추가 수정
        Page<ArticleResponseV1> articles = articleService.searchArticles(searchType,searchValue, pageable).map(ArticleResponseV1::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(),articles.getTotalPages());
        map.addAttribute("articles",articles);
        map.addAttribute("paginationBarNumbers",barNumbers);
        map.addAttribute("searchTypes",SearchType.values()); // 검색
        return "articles/index";
    }

    @GetMapping("/{articleId}")// 게시글 단건(상세) 페이지 - 정상 호출
    public String article(@PathVariable Long articleId, ModelMap map){
        ArticleWithCommentsResponseV1 article = ArticleWithCommentsResponseV1.from(articleService.getArticle(articleId));
        map.addAttribute("article",article);
        map.addAttribute("articleComments", article.articleCommentsResponse());
        return "articles/detail";
    }


}
