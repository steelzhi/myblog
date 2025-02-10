package ru.yandex.practicum.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.configuration.WebConfiguration;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Tag;

import java.sql.ResultSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        jdbcTemplate.execute("DELETE FROM posts_tags;");
        jdbcTemplate.execute("DELETE FROM comments;");
        jdbcTemplate.execute("DELETE FROM posts;");
        jdbcTemplate.execute("DELETE FROM tags;");
        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE tags ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE posts_tags ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("INSERT INTO posts (name, text) VALUES ('Post1', 'Text1')");
        jdbcTemplate.execute("INSERT INTO posts (name, text) VALUES ('Post2', 'Text2')");
        jdbcTemplate.execute("INSERT INTO posts (name, text) VALUES ('Post3', 'Text3')");
        jdbcTemplate.execute("INSERT INTO tags (text) VALUES ('#Tag1')");
        jdbcTemplate.execute("INSERT INTO tags (text) VALUES ('#Tag2')");
        jdbcTemplate.execute("INSERT INTO tags (text) VALUES ('#CommonTag')");
        jdbcTemplate.execute("INSERT INTO posts_tags (post_id, tag_id) VALUES (1, 1)");
        jdbcTemplate.execute("INSERT INTO posts_tags (post_id, tag_id) VALUES (2, 2)");
        jdbcTemplate.execute("INSERT INTO posts_tags (post_id, tag_id) VALUES (1, 3)");
        jdbcTemplate.execute("INSERT INTO posts_tags (post_id, tag_id) VALUES (2, 3)");
    }

    @Test
    void addPostDto_shouldAddPostDtoToDatabaseAndRedirect() throws Exception {
        mockMvc.perform(post("/feed")
                        .param("name", "Post4")
                        .param("text", "Text4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));
    }

    @Test
    void addLike_shouldAddLikeAndRedirect() throws Exception {
        mockMvc.perform(post("/feed/post/1/addLike"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));
    }

    @Test
    void addComment_shouldAddCommentAndRedirect() throws Exception {
        mockMvc.perform(post("/feed/post/1/addComment")
                .param("text", "new comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));
    }

    @Test
    void getFeed_shouldReturnHtmlWithFeed() throws Exception {
        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(3 * 7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());

    }

    @Test
    void getFeedWithChosenTags_shouldReturnHtmlWithFeedWithChosenTags() throws Exception {
        mockMvc.perform(get("/feed/tags/")
                        .param("tagsString", "Tag1, #Tag2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(7))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());

    }

    @Test
    void getPostById_shouldReturnPostById() throws Exception {
        mockMvc.perform(get("/feed/post/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("postDto"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(6))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").exists());

    }

    @Test
    void getFeedSplittedByPages_shouldReturnFeedSplittedBy2Pages() throws Exception {
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
        mockMvc.perform(post("/feed/post/1/change")
                        .param("name", "Changed Post1")
                        .param("text", "Changed Text1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/1"));
    }

    @Test
    void deletePost_shouldDeletePostAndRedirect() throws Exception {
        mockMvc.perform(post("/feed/post/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));

    }

    @Test
    void deleteComment_shouldDeleteCommentAndRedirect() throws Exception {
        mockMvc.perform(post("/feed/post/2/removeComment/1")
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed/post/2"));

    }
}
