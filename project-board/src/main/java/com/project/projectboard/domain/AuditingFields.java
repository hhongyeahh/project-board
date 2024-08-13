package com.project.projectboard.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)//auditing 기능 동작하게 함
@MappedSuperclass
public abstract class AuditingFields { //별개의 엔티티가 아닌 다른 엔티티 클래스에 공통으로 들어갈 것이니 추상클래스로 설정해 준다.

    //메타데이터 - 자동세팅기능 - JPA auditing
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)//Web화면에서 파라미터를 받아서 세팅할 때 파싱에 대한 룰을 넣어줘야함
    @CreatedDate
    @Column(nullable = false,updatable = false)//이 필드는 업데이트 불가함
    private LocalDateTime createdAt; // 생성일시

    @CreatedBy
    @Column(nullable = false, length = 100,updatable = false)
    private String createdBy; // 생성자

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;  //수정일시
    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy; // 수정자

}
