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
public class PostControllerAllLayersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    public void setUp() {
        // Очистка базы данных перед каждым тестом
        postRepository.cleanAllPosts();
        postRepository.cleanTagsMap();
    }

    @Test
    void addPostDto_shouldAddPostDtoToDatabaseAndRedirect() throws Exception {
        Post post = new Post(0, "Post", null, "Text", "Tag");

        mockMvc.perform(post("/feed")
                        .param("id", String.valueOf(post.getId()))
                        .param("name", post.getName())
                        .param("text", post.getText())
                        .param("tagsString", post.getTagsString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));

        PostResponseDto addedPostDto = postRepository.getPostById(1);
        assertTrue(addedPostDto.getName().equals(post.getName()), "Posts names don't match");
        assertTrue(addedPostDto.getText().equals(post.getText()), "Posts texts don't match");
    }

    @Test
    void getFeedWithChosenTags_shouldReturnHtmlWithFeedWithChosenTags() throws Exception {
        PostRequestDto postDto1
                = new PostRequestDto(1, "Post1", null, "Text1", "#Tag1");
        PostRequestDto postDto2
                = new PostRequestDto(2, "Post2", null, "Text2", "#Tag2");
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
        PostRequestDto postDto1
                = new PostRequestDto(1, "Post1", null, "Text1", "#Tag1");
        postRepository.addPostDto(postDto1);

        mockMvc.perform(get("/feed/post/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("postResponseDto"));
    }

    @Test
    void getFeedSplittedByPagesWithPageParams_shouldReturnFeedSplittedBy2Pages() throws Exception {
        PostRequestDto postDto1
                = new PostRequestDto(1, "Post1", null, "Text1", "#Tag1");
        PostRequestDto postDto2
                = new PostRequestDto(2, "Post2", null, "Text2", "#Tag2");
        PostRequestDto postDto3
                = new PostRequestDto(3, "Post3", null, "Text3", "#Tag3");
        postRepository.addPostDto(postDto1);
        postRepository.addPostDto(postDto2);
        postRepository.addPostDto(postDto3);

        mockMvc.perform(get("/feed")
                        .param("postsOnPage", "2")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(2 * 7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());
    }

    @Test
    void getFeedSplittedByPagesWithoutPageParams_shouldReturnFeed() throws Exception {
        PostRequestDto postDto1
                = new PostRequestDto(1, "Post1", null, "Text1", "#Tag1");
        PostRequestDto postDto2
                = new PostRequestDto(2, "Post2", null, "Text2", "#Tag2");
        PostRequestDto postDto3
                = new PostRequestDto(3, "Post3", null, "Text3", "#Tag3");
        postRepository.addPostDto(postDto1);
        postRepository.addPostDto(postDto2);
        postRepository.addPostDto(postDto3);

        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(3 * 7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());
    }

    @Test
    void changePost_shouldChangePostAndRedirect() throws Exception {
        PostRequestDto postDto1
                = new PostRequestDto(1, "Post1", null, "Text1", "#Tag1");
        postRepository.addPostDto(postDto1);

        mockMvc.perform(post("/feed/post/1/change")
                        .param("name", "Changed Post1")
                        .param("text", "Changed Text1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));

        PostResponseDto changedPostDto = postRepository.getPostById(1);
        assertEquals(changedPostDto.getName(), "Changed Post1", "Incorrect post name");
        assertEquals(changedPostDto.getText(), "Changed Text1", "Incorrect text name");
    }

    @Test
    void deletePost_shouldDeletePostAndRedirect() throws Exception {
        PostRequestDto postDto1
                = new PostRequestDto(1, "Post1", null, "Text1", "#Tag1");
        postRepository.addPostDto(postDto1);

        mockMvc.perform(post("/feed/post/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));
        int feedSize = postRepository.getFeedSize();
        assertTrue(feedSize == 0, "Post was removedIncorrectly");
    }
}
