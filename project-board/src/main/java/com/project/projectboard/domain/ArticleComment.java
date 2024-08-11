package com.project.projectboard.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
//@NoArgsConstructor(access = AccessLevel.PROTECTED) -> 애노테이션으로 기본 생성자 생성 가능
@EntityListeners(AuditingEntityListener.class)//auditing 기능 동작하게 ㅎ함
public class ArticleComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false) //cascade 주지 않음 -> 댓글이 지워진다고 게시글이 영향을 받으면 안됨 기본값 none
    private Article article; // 게시글(ID)
    @Setter
    @Column(nullable = false,length = 500)
    private String content; // 본문

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일시
    @CreatedBy
    @Column(nullable = false)
    private String createdBy; // 생성자
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;  //수정일시
    @LastModifiedBy
    @Column(nullable = false)
    private String modifiedBy; // 수정자

    protected ArticleComment() {
    }

    private ArticleComment(Article article, String content) {
        this.article = article;
        this.content = content;
    }
    public static ArticleComment of(Article article, String content) {
        return new ArticleComment(article,content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment articleComment)) return false;
        return id != null && id.equals(articleComment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
