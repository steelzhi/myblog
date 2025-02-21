package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.ImageService;
import ru.yandex.practicum.service.LikeService;
import ru.yandex.practicum.service.PostService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void addComment_shouldAddCommentAndRedirect() throws Exception {
        Comment comment = new Comment(1, 1, "new comment");
        PostResponseDto postDtoWithComment
                = new PostResponseDto(1, "Post", null, "Text", 1, "Tag");
        postDtoWithComment.getCommentsList().add(comment);

        when(commentService.addComment(anyInt(), any(Comment.class)))
                .thenReturn(postDtoWithComment);

        mockMvc.perform(post("/feed/post/1/addComment")
                .param("text", "new comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        verify(commentService, times(1)).addComment(anyInt(), any(Comment.class));
    }

    @Test
    void changeComment_shouldChangeCommentAndRedirect() throws Exception {
        PostResponseDto postDtoWithChangedComment
                = new PostResponseDto(1, "Post1", null, "Text1", 1, "Tag1");
        Comment changedComment = new Comment(1, 1, "Changed comment Text1");
        postDtoWithChangedComment.getCommentsList().add(changedComment);

        when(commentService.changeComment(1, 1, "Changed comment Text1"))
                .thenReturn(postDtoWithChangedComment);

        mockMvc.perform(post("/feed/post/comment")
                        .param("id", "1")
                        .param("postId", "1")
                        .param("text", "Changed comment Text1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        verify(commentService, times(1)).changeComment(1, 1, "Changed comment Text1");
    }

    @Test
    void deleteComment_shouldDeleteCommentAndRedirect() throws Exception {
        when(commentService.deleteComment(1, 1))
                .thenReturn(
                        new PostResponseDto(1, "Post1", null, "Text1", 1, "Tag1"));

        mockMvc.perform(post("/feed/post/1/removeComment/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        verify(commentService, times(1)).deleteComment(1, 1);
    }
}
