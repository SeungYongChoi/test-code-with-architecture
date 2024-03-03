package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.dto.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {
    @Test
    void 사용자는_특정_유저의_주소를_제외한_정보를_전달_받을_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.ACTIVE)
                .build());

        UserController userController = UserController.builder()
                .userReadService(testContainer.userReadService)
                .build();
        // when
        ResponseEntity<UserResponse> result = userController.getUserById(1);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("test@test.com");
        assertThat(result.getBody().getNickname()).isEqualTo("testNickname");
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_존재하지_않는_유저의_아이디로_api_호출할_경우_404_응답을_받는다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.ACTIVE)
                .build());

        UserController userController = UserController.builder()
                .userReadService(testContainer.userReadService)
                .build();

        // when
        // then
        assertThatThrownBy(() -> {
            userController.getUserById(2L);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 사용자는_인증_코드로_계정을_활성화_시킬_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.PENDING)
                .build());

        UserController userController = testContainer.userController;
        // then
        ResponseEntity<Void> result = userController.verifyEmail(1L, "aaaa-aaa-aaa-aaaa");

        // when
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(302));
        assertThat(testContainer.userRepository.getById(1).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_잘못된_인증_코드로_계정을_활성화_시킬_시_에러를_반환한다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.PENDING)
                .build());

        UserController userController = testContainer.userController;
        // when
        // then
        assertThatThrownBy(() -> {
            userController.verifyEmail(1L, "aaaa-aaa-aaa-aaaabb");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
    @Test
    void 사용자는_내_정보를_불러올_때_개인정보인_주소도_갖고_올_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(new TestClockHolder(100000))
                .build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .build());
        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.getMyInfo("test@test.com");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getEmail()).isEqualTo("test@test.com");
        assertThat(result.getBody().getNickname()).isEqualTo("testNickname");
        assertThat(result.getBody().getAddress()).isEqualTo("Bundang");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100000);
    }

    @Test
    void 사용자는_내_정보를_수정_할_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(new TestClockHolder(1000))
                .build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.ACTIVE)
                .build());

        UserUpdate userUpdateDto = UserUpdate.builder()
                .nickname("yong22")
                .address("Pangyo")
                .build();
        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.updateMyInfo("test@test.com", userUpdateDto);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("test@test.com");
        assertThat(result.getBody().getNickname()).isEqualTo("yong22");
        assertThat(result.getBody().getAddress()).isEqualTo("Pangyo");
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}