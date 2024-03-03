package com.example.demo.medium;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@SqlGroup({
        @Sql(value = "/sql/post-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@TestPropertySource("classpath:test-application.properties")
class PostServiceTest {
    @Autowired
    PostServiceImpl postService;

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
    }
}