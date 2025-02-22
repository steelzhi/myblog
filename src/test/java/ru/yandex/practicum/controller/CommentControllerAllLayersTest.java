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
public class CommentControllerAllLayersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    public void setUp() {
        // Очистка базы данных перед каждым тестом
        postRepository.cleanAllPosts();
    }

    @Test
    void addComment_shouldAddCommentAndRedirect() throws Exception {
        Post post = new Post(0, "Post", null, "Text", "Tag");
        Comment comment = new Comment(1, 1, "new comment");

        mockMvc.perform(post("/feed")
                .param("id", String.valueOf(post.getId()))
                .param("name", post.getName())
                .param("text", post.getText())
                .param("tagsString", post.getTagsString()));

        mockMvc.perform(post("/feed/post/1/addComment")
                        .param("text", "new comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        PostResponseDto addedPostDto = postRepository.getPostById(1);
        assertEquals(addedPostDto.getCommentsList().size(), 1, "Post should have 1 comment");
        assertEquals(addedPostDto.getCommentsList().getFirst().getText(), comment.getText(),
                "Comment text was saved incorrectly");
    }

    @Test
    void changeComment_shouldChangeCommentAndRedirect() throws Exception {
        PostRequestDto postDto1
                = new PostRequestDto(1, "Post1", null, "Text1", "#Tag1");
        postRepository.addPostDto(postDto1);
        Comment comment = new Comment(1, 1, "new comment");
        commentRepository.addComment(1, comment.getText());

        mockMvc.perform(post("/feed/post/1/comment/1")
                        .param("text", "Changed comment Text1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        PostResponseDto postDtoWithChangedComment = postRepository.getPostById(1);
        assertEquals(postDtoWithChangedComment.getCommentsList().getFirst().getText(),
                "Changed comment Text1", "Comment text was changed incorrectly");
    }

    @Test
    void deleteComment_shouldDeleteCommentAndRedirect() throws Exception {
        PostRequestDto postRequestDto
                = new PostRequestDto(1, "Post1", null, "Text1", "#Tag1");
        Comment comment = new Comment(1, 1, "new comment");
        postRepository.addPostDto(postRequestDto);
        commentRepository.addComment(postRequestDto.getId(), comment.getText());
        PostResponseDto postResponseDto = postRepository.getPostById(postRequestDto.getId());
        assertTrue(postResponseDto.getCommentsList().size() == 1, "Incorrect number of comments");

        mockMvc.perform(post("/feed/post/1/removeComment/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        PostResponseDto postDtoWithDeletedComment = postRepository.getPostById(1);
        assertTrue(postDtoWithDeletedComment.getCommentsList().isEmpty(), "Post still has comment");
    }
}
