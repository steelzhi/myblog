package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class PostControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void addPostDto_shouldAddPostDtoToDatabaseAndRedirect() throws Exception {
        PostResponseDto postDto = new PostResponseDto(1, "Post", null, "Text", 0, "Tag");
        Mockito.when(postService.addPost(any(Post.class)))
                        .thenReturn(postDto);

        mockMvc.perform(post("/feed")
                        .param("id", "0")
                        .param("name", "Post")
                        .param("text", "Text")
                        .param("tagsString", "Tag"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));

        verify(postService, times(1)).addPost(any(Post.class));
    }

    @Test
    void addLike_shouldAddLikeAndRedirect() throws Exception {
        PostResponseDto postDtoWithLike
                = new PostResponseDto(1, "Post", null, "Text", 1, "Tag");

        when(postService.addLike(1))
                .thenReturn(postDtoWithLike);

        mockMvc.perform(post("/feed/post/1/addLike"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        verify(postService, times(1)).addLike(1);

    }

    @Test
    void addComment_shouldAddCommentAndRedirect() throws Exception {
        Comment comment = new Comment(1, 1, "new comment");
        PostResponseDto postDtoWithComment
                = new PostResponseDto(1, "Post", null, "Text", 1, "Tag");
        postDtoWithComment.getCommentsList().add(comment);

        when(postService.addComment(anyInt(), any(Comment.class)))
                .thenReturn(postDtoWithComment);

        mockMvc.perform(post("/feed/post/1/addComment")
                .param("text", "new comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        verify(postService, times(1)).addComment(anyInt(), any(Comment.class));
    }

    @Test
    void getFeed_shouldReturnHtmlWithFeed() throws Exception {
        when(postService.getSortedFeed())
                .thenReturn(List.of(
                        new PostResponseDto(1, "Post1", null, "Text1", 1, "Tag1"),
                        new PostResponseDto(2, "Post2", null, "Text2", 1, "Tag2")));

        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(2 * 7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());

        verify(postService, times(1)).getSortedFeed();
    }

    @Test
    void getFeedWithChosenTags_shouldReturnHtmlWithFeedWithChosenTags() throws Exception {
        when(postService.getFeedWithChosenTags("#Tag1"))
                .thenReturn(List.of(
                        new PostResponseDto(1, "Post1", null, "Text1", 1, "#Tag1")));

        mockMvc.perform(get("/feed/tags/")
                        .param("tagsString", "#Tag1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(1 * 7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());

        verify(postService, times(1)).getFeedWithChosenTags("#Tag1");
    }

    @Test
    void getPostById_shouldReturnPostById() throws Exception {
        when(postService.getPostById(1))
                .thenReturn(
                        new PostResponseDto(1, "Post1", null, "Text1", 1, "#Tag1"));

        mockMvc.perform(get("/feed/post/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("postResponseDto"));

        verify(postService, times(1)).getPostById(1);
    }

    @Test
    void getFeedSplittedByPages_shouldReturnFeedSplittedBy2Pages() throws Exception {
        when(postService.getFeedSplittedByPages(2, 1))
                .thenReturn(List.of(
                        new PostResponseDto(1, "Post1", null, "Text1", 1, "Tag1"),
                        new PostResponseDto(2, "Post2", null, "Text2", 1, "Tag2")));

        mockMvc.perform(get("/feed/pages/2/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(2 * 7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());

        verify(postService, times(1)).getFeedSplittedByPages(2, 1);
    }

    @Test
    void changePost_shouldChangePostAndRedirect() throws Exception {
        when(postService.changePost(any(Post.class)))
                .thenReturn(
                        new PostResponseDto(1, "Changed Post1", null, "Changed Text1", 1, "Changed Tag1"));
        mockMvc.perform(post("/feed/post/1/change")
                        .param("name", "Changed Post1")
                        .param("text", "Changed Text1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        verify(postService, times(1)).changePost(any(Post.class));
    }

    @Test
    void changeComment_shouldChangeCommentAndRedirect() throws Exception {
        PostResponseDto postDtoWithChangedComment
                = new PostResponseDto(1, "Post1", null, "Text1", 1, "Tag1");
        Comment changedComment = new Comment(1, 1, "Changed comment Text1");
        postDtoWithChangedComment.getCommentsList().add(changedComment);

        when(postService.changeComment(1, 1, "Changed comment Text1"))
                .thenReturn(postDtoWithChangedComment);

        mockMvc.perform(post("/feed/post/comment")
                        .param("id", "1")
                        .param("postId", "1")
                        .param("text", "Changed comment Text1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        verify(postService, times(1)).changeComment(1, 1, "Changed comment Text1");
    }

    @Test
    void deletePost_shouldDeletePostAndRedirect() throws Exception {
        doNothing().when(postService).deletePost(1);

        mockMvc.perform(post("/feed/post/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));

        verify(postService, times(1)).deletePost(1);
    }

    @Test
    void deleteComment_shouldDeleteCommentAndRedirect() throws Exception {
        when(postService.deleteComment(1, 1))
                .thenReturn(
                        new PostResponseDto(1, "Post1", null, "Text1", 1, "Tag1"));

        mockMvc.perform(post("/feed/post/1/removeComment/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        verify(postService, times(1)).deleteComment(1, 1);
    }
}
