package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.dto.UserCreate;
import com.example.demo.user.domain.dto.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

//@DataJpaTest(showSql = true)

class UserServiceTest {

    UserServiceImpl userService;
    @BeforeEach
    void init() {



    }
    @Test
    void getByEmail_은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        String email = "ppyooy336@naver.com";

        // when
        User result = userService.getByEmail(email);

        // then
        assertThat(result.getNickname()).isEqualTo("yong");
    }

    @Test
    void getByEmail_은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        String email = "ppyooy337@naver.com";

        // when
        // then
        assertThatThrownBy(() -> {
            User result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById_은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        String email = "ppyooy336@naver.com";

        // when
        User result = userService.getById(1);

        // then
        assertThat(result.getNickname()).isEqualTo("yong");
    }

    @Test
    void getById_은_PENDING_상태인_유저를_찾아올_수_없다() {
        // given
        String email = "ppyooy337@naver.com";

        // when
        // then
        assertThatThrownBy(() -> {
            User result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreate를_이용하여_유저를_생성할_수_있다() {
        // given
        UserCreate userCreateDto = UserCreate.builder()
                .email("ppyooy336@gmail.com")
                .nickname("yong23")
                .address("yongin")
                .build();

        // when
        User result = userService.create(userCreateDto);
        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo("aaaaa-aaaa-aaa-aaaa");
    }

    @Test
    void userUpdateDto를_이용하여_유저를_수정할_수_있다() {
        // given
        UserUpdate userUpdateDto = UserUpdate.builder()
                .nickname("yong23")
                .address("Gwang-ju")
                .build();

        // when
        userService.update(1, userUpdateDto);

        // then
        User result = userService.getById(1);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAddress()).isEqualTo("Gwang-ju");
        assertThat(result.getNickname()).isEqualTo("yong23");
    }

    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다() {
        // when
        userService.login(1);

        // then
        User result = userService.getById(1);
        assertThat(result.getLastLoginAt()).isGreaterThan(0);
        assertThat(result.getLastLoginAt()).isEqualTo(1234980518);
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_시킬_수_있다() {
        // when
        userService.verifyEmail(2, "aaaaa-aaaa-aaa-aaaa");

        // then
        User result = userService.getById(2);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증_코드를_받으면_에러를_던진다() {

        // when
        // then
        assertThatThrownBy(() -> {
            userService.verifyEmail(2, "aaaaa-aaaa-aaa-aaaabb");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}