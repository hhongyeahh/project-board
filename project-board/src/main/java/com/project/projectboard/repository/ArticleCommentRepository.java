package com.project.projectboard.repository;

import com.project.projectboard.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {//id 타입이 Long이라
}
