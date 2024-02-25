package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    public void PostCreate_로_Post_를_생성할_수_있다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .address("Bundang")
                .certificationCode("aaaa-aaa-aaa-aaaa")
                .status(UserStatus.ACTIVE)
                .build();

        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("test-content")
                .build();

        // when
        Post post = Post.from(postCreate, user);

        // then
        assertThat(post.getContent()).isEqualTo("test-content");
        assertThat(post.getWriter().getEmail()).isEqualTo("test@test.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("testNickname");
        assertThat(post.getWriter().getAddress()).isEqualTo("Bundang");
        assertThat(post.getWriter().getCertificationCode()).isEqualTo("aaaa-aaa-aaa-aaaa");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void PostUpdate_로_Post_의_데이터를_업데이트_할_수_있다() {

    }
}