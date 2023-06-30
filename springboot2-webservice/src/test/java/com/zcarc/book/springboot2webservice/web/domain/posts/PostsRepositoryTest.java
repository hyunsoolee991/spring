package com.zcarc.book.springboot2webservice.web.domain.posts;

import com.zcarc.book.springboot2webservice.domain.posts.Posts;
import com.zcarc.book.springboot2webservice.domain.posts.PostsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @AfterEach
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        // given
        String title = "테스트 게시글";
        String content = "테스트 본문";

        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("wrkbrs@gmail.com")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAll();

        // then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);

    }

    @Test
    public void BaseTimeEntity_등록() {

        // given
        LocalDateTime now = LocalDateTime.of(2022, 12, 17, 0, 0, 0);

        postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAll();

        for (Posts posts : postsList) {
            System.out.println("posts.getId() = " + posts.getId());
            System.out.println("posts.getTitle() = " + posts.getTitle());
            System.out.println("posts.getContent() = " + posts.getContent());
            System.out.println("posts.getCreatedDate() = " + posts.getCreatedDate());
            System.out.println("posts.getAuthor() = " + posts.getAuthor());
            System.out.println("posts.getModifiedDate() = " + posts.getModifiedDate());
        }


        // then
        Posts posts = postsList.get(0);

        System.out.println(">>>>>>> createdDate=" + posts.getCreatedDate() + ", modifiedDate=" + posts.getModifiedDate());

        assertThat(posts.getCreatedDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);

    }

}