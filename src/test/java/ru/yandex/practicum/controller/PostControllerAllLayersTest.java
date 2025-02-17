package ru.yandex.practicum.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test-application.properties")
@Sql("classpath:test-schema.sql")
public class PostControllerAllLayersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    public void setUp() {
        // Очистка базы данных перед каждым тестом
        postRepository.cleanAllDataBase();
    }

    @Test
    void addPostDto_shouldAddPostDtoToDatabaseAndRedirect() throws Exception {
        Post post = new Post(0, "Post", null, "Text", "Tag");
        /*PostDto postDto = PostMapper.mapToPostDto(post);
        postRepository.addPostDto(postDto);*/

        mockMvc.perform(post("/feed")
                        .param("id", String.valueOf(post.getId()))
                        .param("name", post.getName())
                        .param("text", post.getText())
                        .param("tagsString", post.getTagsString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));

        PostDto addedPostDto = postRepository.getPostById(1);
        assertTrue(addedPostDto.getName().equals(post.getName()), "Posts names don't match");
        assertTrue(addedPostDto.getText().equals(post.getText()), "Posts texts don't match");
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

        PostDto addedPostDto = postRepository.getPostById(1);
        assertEquals(addedPostDto.getNumberOfLikes(), 1, "Post should have 1 like");
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

        PostDto addedPostDto = postRepository.getPostById(1);
        assertEquals(addedPostDto.getCommentsList().size(), 1, "Post should have 1 comment");
        assertEquals(addedPostDto.getCommentsList().getFirst().getText(), comment.getText(), "Comment text was saved incorrectly");
    }

    @Test
    void getFeed_shouldReturnHtmlWithFeed() throws Exception {
        PostDto postDto1 = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        PostDto postDto2 = new PostDto(2, "Post2", null, "Text2", 1, "#Tag2");
        postRepository.addPostDto(postDto1);
        postRepository.addPostDto(postDto2);

        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(2 * 7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());
    }

    @Test
    void getFeedWithChosenTags_shouldReturnHtmlWithFeedWithChosenTags() throws Exception {
        PostDto postDto1 = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        PostDto postDto2 = new PostDto(2, "Post2", null, "Text2", 1, "#Tag2");
        postRepository.addPostDto(postDto1);
        postRepository.addPostDto(postDto2);

        mockMvc.perform(get("/feed/tags/")
                        .param("tagsString", "#Tag1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(1 * 7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());
    }

    @Test
    void getPostById_shouldReturnPostById() throws Exception {
        PostDto postDto1 = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        postRepository.addPostDto(postDto1);

        mockMvc.perform(get("/feed/post/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("postDto"));
    }

    @Test
    void getFeedSplittedByPages_shouldReturnFeedSplittedBy2Pages() throws Exception {
        PostDto postDto1 = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        PostDto postDto2 = new PostDto(2, "Post2", null, "Text2", 1, "#Tag2");
        PostDto postDto3 = new PostDto(3, "Post3", null, "Text3", 1, "#Tag3");
        postRepository.addPostDto(postDto1);
        postRepository.addPostDto(postDto2);
        postRepository.addPostDto(postDto3);

        mockMvc.perform(get("/feed/pages/2/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(2 * 7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());

    }

    @Test
    void changePost_shouldChangePostAndRedirect() throws Exception {
        PostDto postDto1 = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        postRepository.addPostDto(postDto1);

        mockMvc.perform(post("/feed/post/1/change")
                        .param("name", "Changed Post1")
                        .param("text", "Changed Text1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        PostDto changedPostDto = postRepository.getPostById(1);
        assertEquals(changedPostDto.getName(), "Changed Post1", "Incorrect post name");
        assertEquals(changedPostDto.getText(), "Changed Text1", "Incorrect text name");
    }

    @Test
    void changeComment_shouldChangeCommentAndRedirect() throws Exception {
        PostDto postDto1 = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        postRepository.addPostDto(postDto1);
        Comment comment = new Comment(1, 1, "new comment");
        postRepository.addComment(1, comment.getText());

        mockMvc.perform(post("/feed/post/comment")
                        .param("id", "1")
                        .param("postId", "1")
                        .param("text", "Changed comment Text1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        PostDto postDtoWithChangedComment = postRepository.getPostById(1);
        assertEquals(postDtoWithChangedComment.getCommentsList().getFirst().getText(),
                "Changed comment Text1", "Comment text was changed incorrectly");
    }

    @Test
    void deletePost_shouldDeletePostAndRedirect() throws Exception {
        PostDto postDto1 = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        postRepository.addPostDto(postDto1);

        mockMvc.perform(post("/feed/post/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));
        List<PostDto> postDtoList = postRepository.getSortedFeed();
        assertTrue(postDtoList.isEmpty(), "Post was removedIncorrectly");
    }

    @Test
    void deleteComment_shouldDeleteCommentAndRedirect() throws Exception {
        PostDto postDto1 = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        Comment comment = new Comment(1, 1, "new comment");
        postDto1.getCommentsList().add(comment);
        postRepository.addPostDto(postDto1);

        mockMvc.perform(post("/feed/post/1/removeComment/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        PostDto postDtoWithDeletedComment = postRepository.getPostById(1);
        assertTrue(postDtoWithDeletedComment.getCommentsList().isEmpty(), "Post still has comment");
    }
}
