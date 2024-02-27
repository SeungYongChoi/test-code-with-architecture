package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.dto.UserCreate;
import com.example.demo.user.domain.dto.UserUpdate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    public void User_는_UserCreate_객체로_생성할_수_있다() {
        //given
        UserCreate userCreateDto = UserCreate.builder()
                .email("ppyooy336@gmail.com")
                .nickname("yong23")
                .address("yongin")
                .build();
        TestUuidHolder testUuidHolder = new TestUuidHolder("aaaaa-aaaa-aaa-aaaa");
        //when

        User user = User.from(userCreateDto, testUuidHolder);
        //then
        assertThat(user.getEmail()).isEqualTo("ppyooy336@gmail.com");
        assertThat(user.getNickname()).isEqualTo("yong23");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getAddress()).isEqualTo("yongin");
        assertThat(user.getCertificationCode()).isEqualTo("aaaaa-aaaa-aaa-aaaa");
    }

    @Test
    public void User_는_UserUpdate_객체로_데이터를_업데이트_할_수_있다() {
        //given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .build();

        UserUpdate userUpdateDto = UserUpdate.builder()
                .nickname("updateNickname")
                .address("Pangyo")
                .build();
        //when

        User result = user.update(userUpdateDto);

        //then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getNickname()).isEqualTo("updateNickname");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getAddress()).isEqualTo("Pangyo");
        assertThat(result.getCertificationCode()).isEqualTo("aaaa-aaa-aaa-aaaa");
        assertThat(result.getLastLoginAt()).isEqualTo(100L);
    }

    @Test
    public void User_는_로그인을_할_수_있고_로그인시_마지막_로그인_시간이_변경된다() {
        //given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .build();
        TestClockHolder testClockHolder = new TestClockHolder(120L);

        // when
        User result = user.login(testClockHolder);

        //then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getNickname()).isEqualTo("testNickname");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getAddress()).isEqualTo("Bundang");
        assertThat(result.getCertificationCode()).isEqualTo("aaaa-aaa-aaa-aaaa");
        assertThat(result.getLastLoginAt()).isEqualTo(120L);

    }

    @Test
    public void User_는_유효한_인증_코드로_계정을_활성화_할_수_있다() {
        //given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .build();

        // when
        User result = user.certificate("aaaa-aaa-aaa-aaaa");

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getNickname()).isEqualTo("testNickname");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getAddress()).isEqualTo("Bundang");
        assertThat(result.getCertificationCode()).isEqualTo("aaaa-aaa-aaa-aaaa");
        assertThat(result.getLastLoginAt()).isEqualTo(100L);
    }

    @Test
    public void User_는_유호하지_않은_인증_코드로_계정을_활성화_하려하면_에러를_던진다() {
        //given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .build();

        // when
        assertThatThrownBy(() -> {
            User result = user.certificate("aaaa-aaa-aaa-aabb");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}