package ru.yandex.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:test-application.properties")
@Sql("classpath:test-schema.sql")
public class PostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
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
        jdbcTemplate.execute("INSERT INTO comments (post_id, text) VALUES (2, 'Comment text')");
    }

    @Test
    void addPostDto_shouldAddPostDtoToDatabase() {
        PostRequestDto postDto = new PostRequestDto(
                0, "Post3", null, "Text3", "#Tag1");
        PostResponseDto savedPostDto = postRepository.addPostDto(postDto);
        assertNotNull(savedPostDto);
        assertEquals(postDto.getName(), savedPostDto.getName(), "Post names don't match");
        assertEquals(postDto.getText(), savedPostDto.getText(), "Post texts don't match");
        assertEquals(postDto.getTagsTextList(), savedPostDto.getTagsTextList(), "Post tags don't match");
    }

    @Test
    void getSortedFeed_shouldReturnSortedFeed() {
        List<PostResponseDto> feed = postRepository.getSortedFeed();
        assertEquals(feed.size(), 3, "Feed should contain 3 posts");
        PostResponseDto postDto3 = postRepository.getPostById(3);
        assertEquals(feed.get(0), postDto3, "Post 3 should be the first in feed");
    }

    @Test
    void getFeedWithChosenTags_shouldReturnFeedWithChosenTags() throws SQLException {
        String tagString1 = "('#Tag1')";
        String tagString2 = "('#CommonTag','#Tag2')";
        List<PostResponseDto> feedWithChosenTag1 = postRepository.getFeedWithChosenTags(tagString1);
        assertTrue(feedWithChosenTag1.size() == 1, "Feed should contain 1 post with #Tag1");
        List<PostResponseDto> feedWithChosenTags2AndCommon = postRepository.getFeedWithChosenTags(tagString2);
        assertTrue(feedWithChosenTags2AndCommon.size() == 2,
                "Feed should contain 2 posts with #Tag2 and #CommonTag");
    }

    @Test
    void getPostById_shouldReturnPostById() {
        PostResponseDto postDto1 = postRepository.getPostById(1);
        assertTrue(postDto1 != null, "Post 1 should exist");
        assertEquals(postDto1.getName(), "Post1", "Name of Post 1 is incorrect");
        assertEquals(postDto1.getText(), "Text1", "Text of Post 1 is incorrect");
        assertEquals(postDto1.getTagsTextList().size(), 2, "Post 1 should contain 2 tags");
    }

    @Test
    void getFeedSplittedByPages_shouldReturnFeedSplittedBy2Pages() {
        List<PostResponseDto> feedSplittedBy2Pages1 = postRepository.getFeedSplittedByPages(2, 1);
        assertTrue(feedSplittedBy2Pages1.size() == 2, "First page should contain 2 posts");
        PostResponseDto postDto3 = postRepository.getPostById(3);
        PostResponseDto postDto2 = postRepository.getPostById(2);
        PostResponseDto firstPostInFeedSplittedBy2Pages1 = feedSplittedBy2Pages1.get(0);
        PostResponseDto secondPostInFeedSplittedBy2Pages1 = feedSplittedBy2Pages1.get(1);
        assertEquals(firstPostInFeedSplittedBy2Pages1.getName(), postDto3.getName(),
                "First on page 1 should be post 3");
        assertEquals(secondPostInFeedSplittedBy2Pages1.getText(), postDto2.getText(),
                "Second on page 1 should be post 2");

        List<PostResponseDto> feedSplittedBy2Pages2 = postRepository.getFeedSplittedByPages(2, 2);
        assertTrue(feedSplittedBy2Pages2.size() == 1, "Second page should contain 2 posts");
        PostResponseDto postDto1 = postRepository.getPostById(1);
        PostResponseDto firstPostInFeedSplittedBy2Pages2 = feedSplittedBy2Pages2.get(0);
        assertEquals(firstPostInFeedSplittedBy2Pages2.getName(), postDto1.getName(),
                "First on page 2 should be post 1");
        assertEquals(firstPostInFeedSplittedBy2Pages2.getText(), postDto1.getText(),
                "First on page 2 should be post 1");
    }

    @Test
    void changePost_shouldChangePost() {
        PostRequestDto changedPostRequestDto = new PostRequestDto(1, "Changed post 1", null, "Changed text 1", "#Tag1");
        PostResponseDto changedPostResponseDto = postRepository.changePost(changedPostRequestDto);

        assertEquals(changedPostRequestDto.getName(), changedPostResponseDto.getName(), "Name was changed incorrect");
        assertEquals(changedPostRequestDto.getText(), changedPostResponseDto.getText(), "Text was changed incorrect");
        assertEquals(changedPostRequestDto.getTagsTextList(), changedPostResponseDto.getTagsTextList(),
                "Tags were changed incorrectly");
    }

    @Test
    void deletePost_shouldDeletePost() {
        PostResponseDto postDto1 = postRepository.getPostById(1);
        postRepository.deletePost(postDto1.getId());
        List<PostResponseDto> feedWithoutPost1 = postRepository.getSortedFeed();
        assertFalse(feedWithoutPost1.contains(postDto1), "Feed shouldn't contain post 1");
        assertTrue(feedWithoutPost1.size() == 2, "Feed should contain only 2 posts");
    }
}
