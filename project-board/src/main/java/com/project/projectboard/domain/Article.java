package com.project.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


@Getter // class 레벨로 Setter를 걸지 않음
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})//사이즈가 너무 큰 본문은 보통 인덱스를 걸지 않음 - ElasticSearch와 같은 검색엔신의 도움을 받음
//@EntityListeners(AuditingEntityListener.class)//auditing 기능 동작하게 함
@Entity
public class Article extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Setter @Column(nullable = false) // true가 기본
    private String title; // 제목
    @Setter @Column(nullable = false, length = 10000) private String content; // 본문
    @Setter private String hashtag; // 해시태그

    @OrderBy("id")//정렬
    @OneToMany(mappedBy = "article",cascade = CascadeType.ALL)
    //댓글로부터 글을 참조하는 경우는 일반적인데, 글로부터 댓글리스트을 뽑아보는 경우는 덜일반적이라 여기에 exclude 해줌
    @ToString.Exclude//ToString includes lazy loaded fields and/or associations. This can cause performance and memory consumption issues -> circular referencing 문제가 일어날 수 있음
    //collection들어가서 articleComment 안으로 들어가서 또 toString을 보려고 하는데, articleComment안에 toString이 있어서 또 Article이 있어서 Article 보고 무한 반복
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();//이 Article에 연동되어있는 comment는 중복을 허용하지 않고 리스트로 모아서 보여주겠다.

//    //메타데이터 - 자동세팅기능 - JPA auditing
//    @CreatedDate
//    @Column(nullable = false)
//    private LocalDateTime createdAt; // 생성일시
//    @CreatedBy
//    @Column(nullable = false, length = 100)
//    private String createdBy; // 생성자
//    @LastModifiedDate
//    @Column(nullable = false)
//    private LocalDateTime modifiedAt;  //수정일시
//    @LastModifiedBy
//    @Column(nullable = false, length = 100)
//    private String modifiedBy; // 수정자

    protected Article() {
        //모든 hibernate 구현체를 사용하는 JPA entity는 기본생성자를 가지고 있어야함
        //평소에 오픈하지 않을 것이기 때문에 protected
    }

    private Article(String title, String content, String hashtag) {
        //자동 생성되지 않고, 원래 도메인과 연관된 부분만 생성자로 만들 수 있게 유도
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(String title, String content, String hashtag) {
        //new 안쓰고 생성자 메소드 사용할 수 있도록
        return new Article(title,content,hashtag);
    }

    //컬렉션에서 사용할 때 동일성, 동등성 검사 -> EqualsHashCode
    //unique한 id만 가지고 hashing을 하면 됨
    @Override
    public boolean equals(Object o) {
        //엔티티를 DB에 영속화시키고 연결짓고 사용하는 환경에서 서로다른 두 엔티티가 같은 조건이 무엇인가에 대한 질문에 Equals가 답을 함
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return id != null && id.equals(article.id);
        //id != mull : 아직 영속화되지 않은 엔티티는 그 내용이 동등하다고 하더라도 동등성 검사는 탈락한다.
        //pattern matching for instance Java 14
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
