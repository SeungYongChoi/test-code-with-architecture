package com.example.demo.post.service;

import com.example.demo.mock.*;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.CertificationService;
import com.example.demo.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

class PostServiceTest {

    PostService postService;

    @BeforeEach
    public void init() {
        FakeMailSender fakeMailSender = new FakeMailSender();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        TestClockHolder testClockHolder = new TestClockHolder(1234432156L);

        fakeUserRepository.save(User.builder()
                .id(1L)
                .email("ppyooy336@naver.com")
                .nickname("yong")
                .address("Seoul")
                .certificationCode("aaaaa-aaaa-aaa-aaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(1234980518L)
                .build()
        );

        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("ppyooy337@naver.com")
                .nickname("yong")
                .address("Seoul")
                .certificationCode("aaaaa-aaaa-aaa-aaaa")
                .status(UserStatus.PENDING)
                .lastLoginAt(1234980518L)
                .build()
        );

        FakePostRepository fakePostRepository = new FakePostRepository();
        fakePostRepository.save(Post.builder()
                .content("helloworld")
                .createdAt(124215266L)
                .modifiedAt(1234432156L)
                .writer(fakeUserRepository.findById(1L).get())
                .build());

        this.postService = new PostService(fakePostRepository, fakeUserRepository, testClockHolder);
    }
    @Test
    void getPostById는_id로_post를_가져와야한다() {
        // given

        // when
        Post postById = postService.getPostById(1);

        // then
        assertThat(postById.getContent()).isEqualTo("helloworld");
        assertThat(postById.getWriter().getEmail()).isEqualTo("ppyooy336@naver.com");
    }

    @Test
    void create는_새로운_post를_생성해야한다() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("hello~~")
                .build();

        // when
        Post result = postService.create(postCreate);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContent()).isEqualTo("hello~~");
        assertThat(result.getWriter().getEmail()).isEqualTo("ppyooy336@naver.com");
        assertThat(result.getCreatedAt()).isEqualTo(1234432156L);
    }

    @Test
    void update로_기존_post를_수정할_수_있다() {
        // given
        String content = "update hello world";
        PostUpdate postUpdate = PostUpdate.builder()
                .content(content)
                .build();

        // when
        postService.update(1, postUpdate);

        // then
        Post result = postService.getPostById(1);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getModifiedAt()).isEqualTo(1234432156L);
    }
}