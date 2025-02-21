package ru.yandex.practicum.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test-application.properties")
@Sql("classpath:test-schema.sql")
public class LikeControllerAllLayersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    public void setUp() {
        // Очистка базы данных после каждого теста
        postRepository.cleanAllPosts();
    }

    @Test
    void addLike_shouldAddLikeAndRedirect() throws Exception {
        Post post = new Post(0, "Post", null, "Text", "Tag");

        mockMvc.perform(post("/feed")
                .param("id", String.valueOf(post.getId()))
                .param("name", post.getName())
                .param("text", post.getText())
                .param("tagsString", post.getTagsString()));

        mockMvc.perform(post("/feed/post/1/addLike"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        PostResponseDto addedPostDto = postRepository.getPostById(1);
        assertEquals(addedPostDto.getNumberOfLikes(), 1, "Post should have 1 like");
    }
}
