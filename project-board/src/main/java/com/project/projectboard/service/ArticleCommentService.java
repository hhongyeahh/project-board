package com.project.projectboard.service;

import com.project.projectboard.domain.ArticleComment;
import com.project.projectboard.dto.v1.ArticleCommentDtoV1;
import com.project.projectboard.repository.ArticleCommentRepository;
import com.project.projectboard.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Transactional(readOnly = true)//조회
    public List<ArticleCommentDtoV1> searchArticleComment(Long articleId) {
        return List.of();
    }

    @Transactional(readOnly = true)
    public List<ArticleCommentDtoV1> searchArticleComments(Long articleId) {
        return articleCommentRepository.findByArticle_Id(articleId)
                .stream()
                .map(ArticleCommentDtoV1::from)
                .toList();
    }

    public void saveArticleComment(ArticleCommentDtoV1 dto) {

        try {
            articleCommentRepository.save(dto.toEntity(articleRepository.getReferenceById(dto.articleId())));
        }catch (EntityNotFoundException e){
            log.warn("댓글 저장 실패. 댓글의 게시글을 찾을 수 없습니다. - dto: {}", dto);
        }
    }
    public void updateArticleComment(ArticleCommentDtoV1 dto){
        try{
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if(dto.content() != null){
                articleComment.setContent(dto.content());
            }
        }catch (EntityNotFoundException e){
            log.warn("댓글 업데이트 실패. 댓글을 찾을 수 없습니다. - dto: {}", dto);
        }
    }

    public void deleteArticleComment(Long articleCommentId, String userId) {
        articleCommentRepository.deleteById(articleCommentId);

    }
}
