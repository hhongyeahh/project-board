package com.project.projectboard.service;

import com.project.projectboard.dto.v1.ArticleCommentDtoV1;
import com.project.projectboard.repository.ArticleCommentRepository;
import com.project.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<ArticleCommentDtoV1> searchArticleComments(Long articleId) {
        return null;
    }

    public void saveArticleComment(ArticleCommentDtoV1 dto) {


    }

    public void deleteArticleComment(Long articleCommentId, String userId) {

    }
}
