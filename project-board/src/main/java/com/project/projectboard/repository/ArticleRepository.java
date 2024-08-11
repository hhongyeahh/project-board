package com.project.projectboard.repository;

import com.project.projectboard.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//@Repository 스테레오타입 애노테이션 안 붙여도 동작함 -> JPARepository의 구현체인 SimpleRepository에 붙어있음
//기본적으로 스테레오 애노테이션은 인터페이스가 아닌 구현체에 붙임

@RepositoryRestResource
public interface ArticleRepository extends JpaRepository<Article, Long> {
}