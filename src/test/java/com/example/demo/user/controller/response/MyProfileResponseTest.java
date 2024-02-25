package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MyProfileResponseTest {

    @Test
    public void User_로_응답을_생성할_수_가능하다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.ACTIVE)
                .build();

        // when
        MyProfileResponse profileResponse = MyProfileResponse.from(user);

        // then
        assertThat(profileResponse.getId()).isEqualTo(1L);
        assertThat(profileResponse.getEmail()).isEqualTo("test@test.com");
        assertThat(profileResponse.getNickname()).isEqualTo("testNickname");
        assertThat(profileResponse.getAddress()).isEqualTo("Bundang");
        assertThat(profileResponse.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}