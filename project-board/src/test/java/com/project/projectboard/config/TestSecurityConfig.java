package com.project.projectboard.config;

import com.project.projectboard.domain.UserAccount;
import com.project.projectboard.repository.UserAccountRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean private UserAccountRepository userAccountRepository;
    // 이 녀석을 넣게 되면, 각종 컨트롤러 테스트나 insert 테스트에서 이 부분의 문제가 사라지니, 제대로 테스트 할 수 있음


    @BeforeTestMethod
    public void securitySetup(){ //Test 용 계정정보
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "unoTest",
                "pw",
                "uno-test@mail.com",
                "uno-test",
                "test memo"
        )));
    }

}
