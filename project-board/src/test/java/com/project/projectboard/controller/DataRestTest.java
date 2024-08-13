package com.project.projectboard.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Disabled("Spring Data REST 통합테스트는 불필요하므로 제외시킴")
@DisplayName("Data REST - API 테스트")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
//@WebMvcTest//Controller에 대한 Test이니까
public class DataRestTest {

    private final MockMvc mvc;

    public DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[api] 게시글 리스트 조회")
    @Test
    public void givenNothing_whenRequestingArticles_thenReturnsArticlesJsonResponse() throws Exception {

        //given

        //when & Then
        mvc.perform(MockMvcRequestBuilders.get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));

        //Test 오류 뜨는 이유
        //WebMvc는 slice test -> Controller 외에 불필요하다고 여겨지는 Bean들은 로드하지 않음
        //Controller와 연관된 최소한을 로드함 = DataRest의 AutoConfiguration을 넣지 않은 것
        //가장 쉬운 해결책은 IntegrationTest로 작성하는 것 @SpringBootTest
        //따라서 DB에 영향을 주기 때문에 @Transactional을 추가
    }

    @DisplayName("[api] 게시글 단건 조회")
    @Test
    public void givenNothing_whenRequestingArticle_thenReturnsArticlesJsonResponse() throws Exception {

        //given

        //when & Then
        mvc.perform(MockMvcRequestBuilders.get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));

    }

    @DisplayName("[api] 게시글 댓글 조회")
    @Test
    public void givenNothing_whenRequestingArticleCommentsFromArticle_thenReturnsArticlesJsonResponse() throws Exception {

        //given

        //when & Then
        mvc.perform(MockMvcRequestBuilders.get("/api/articles/1/articleComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 게시글 댓글리스트 조회")
    @Test
    public void givenNothing_whenRequestingArticleComments_thenReturnsArticlesJsonResponse() throws Exception {

        //given

        //when & Then
        mvc.perform(MockMvcRequestBuilders.get("/api//articleComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 댓글 단건 조회")
    @Test
    public void givenNothing_whenRequestingArticleComment_thenReturnsArticlesJsonResponse() throws Exception {

        //given

        //when & Then
        mvc.perform(MockMvcRequestBuilders.get("/api/articleComments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 회원 관련 API는 일체 제공하지 않는다.")
    @Test
    void givenNothing_whenRequestIngUserAccount_thenThrowException() throws Exception{
        //given

        //when & then
        mvc.perform(MockMvcRequestBuilders.get("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(MockMvcRequestBuilders.post("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(MockMvcRequestBuilders.put("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(MockMvcRequestBuilders.patch("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(MockMvcRequestBuilders.delete("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(MockMvcRequestBuilders.head("/api/userAccounts")).andExpect(status().isNotFound());

    }
}
