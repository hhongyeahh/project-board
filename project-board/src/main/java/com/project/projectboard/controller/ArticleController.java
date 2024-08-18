package com.project.projectboard.controller;

import com.project.projectboard.domain.constant.FormStatus;
import com.project.projectboard.domain.constant.SearchType;
import com.project.projectboard.dto.UserAccountDto;
import com.project.projectboard.dto.request.ArticleRequest;
import com.project.projectboard.dto.response.ArticleResponse;
import com.project.projectboard.dto.response.ArticleWithCommentsResponse;
import com.project.projectboard.dto.security.BoardPrincipal;
import com.project.projectboard.service.ArticleService;
import com.project.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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

    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping//게시글 리스트 페이지 - 정상 호출
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        //        map.addAttribute("articles", articleService.searchArticles(searchType,searchValue, pageable).map(ArticleResponse::from));
        //페이지네이션 추가 수정
        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values()); // 검색
        return "articles/index";
    }

    @GetMapping("/{articleId}")// 게시글 단건(상세) 페이지 - 정상 호출
    public String article(@PathVariable Long articleId, ModelMap map) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentsResponse());
        map.addAttribute("totalCount", articleService.getArticleCount());
        map.addAttribute("searchTypeHashtag", SearchType.HASHTAG);
        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchArticleHashtag(
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map) {
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        List<String> hashtags = articleService.getHashtags();

        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);
        return "articles/search-hashtag";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }

    @PostMapping("/form")
    public String postNewArticle(ArticleRequest articleRequest,
                                 @AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        articleService.saveArticle(articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId,
                                ArticleRequest articleRequest,
                                @AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        articleService.updateArticle(articleId, articleRequest.toDto(boardPrincipal.toDto()));
        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{articleId}/delete")
    public String deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleService.deleteArticle(articleId, boardPrincipal.getUsername());

        return "redirect:/articles";
    }

}
