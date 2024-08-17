package com.project.projectboard.controller;

import com.project.projectboard.config.SecurityConfig;
import com.project.projectboard.service.ArticleService;
import com.project.projectboard.service.PaginationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 인증")
@WebMvcTest(Void.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
    private final MockMvc mvc;

    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;
    public AuthControllerTest(@Autowired MockMvc mvc){
        this.mvc =mvc;
    }

    @DisplayName("[view][GET] 로그인 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenTryingToLogin_thenReturnLoginView() throws Exception {

        //given

        //when & then
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andDo(MockMvcResultHandlers.print());;

        then(articleService).shouldHaveNoInteractions();
        then(paginationService).shouldHaveNoInteractions();
                //.andExpect(view().name("/login)) // 뷰 내임은 자동생성이라 검사하지 않음

    }
}
