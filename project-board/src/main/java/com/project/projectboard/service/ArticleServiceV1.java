package com.project.projectboard.service;

import com.project.projectboard.domain.Article;
import com.project.projectboard.dto.v1.ArticleDtoV1;
import com.project.projectboard.domain.type.SearchType;
import com.project.projectboard.dto.v1.ArticleWithCommentsDtoV1;
import com.project.projectboard.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleServiceV1 {

    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true) //게시글을 검색하면, 게시글 페이지를 반환한다.
    public Page<ArticleDtoV1> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        //검색어 없이 게시글 검색
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDtoV1::from);
        }

        //검색어와 함께 게시글 검색
        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDtoV1::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDtoV1::from);
            case ID ->
                    articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDtoV1::from);//Article 안 UserAccount
            case NICKNAME ->
                    articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDtoV1::from);//Article 안 UserAccount
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDtoV1::from);//#자동삽입
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDtoV1 getArticle(Long articleId) {
        //단건조회
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDtoV1::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다. - articleId: " + articleId));//게시글 없으면 예외 던짐
    }

    public void saveArticle(ArticleDtoV1 dto) {
        //게시글 생성 및 저장
        articleRepository.save(dto.toEntity());
    }

    public void updateArticle(ArticleDtoV1 dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.id());
            article.setHashtag(dto.hashtag());
            if (dto.title() != null) {
                article.setTitle(dto.title());
            }
            if (dto.content() != null) {
                article.setContent(dto.content());
            }
            //articleRepository.save(article);
            //save 필요없음 updateArticle 은 클래스 레벨 transactional 로 메소드 단위로 transaction 이 묶어있어서
            //transaction 이 끝날때, 영속성 컨텍스트는 Article 의 변화를 감지하고 쿼리문을 날림
        } catch (EntityNotFoundException e) {
            log.warn("게시판 업데이트 실패, 게시글을 찾을 수 없습니다. - dto: {}", dto);
        }
    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);

    }
}