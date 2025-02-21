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
public class CommentRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

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
        jdbcTemplate.execute("INSERT INTO comments (post_id, text) VALUES (2, 'Comment text')");
    }

    @Test
    void addComment_shouldAddCommentToPostDto() throws Exception {
        Comment comment = new Comment(1, 1, "Comment to Post1");
        PostResponseDto postDto1 = postRepository.getPostById(1);
        assertTrue(postDto1.getCommentsList().isEmpty(), "Comments list must be empty");
        commentRepository.addComment(postDto1.getId(), comment.getText());
        PostResponseDto postDto1WithComment = postRepository.getPostById(1);
        assertEquals(postDto1WithComment.getCommentsList().size(), 1,
                "Comments list must contain 1 comment");
        assertEquals(postDto1WithComment.getCommentsList().get(0).getText(), comment.getText(),
                "Comment text was added incorrectly");
    }

    @Test
    void changeComment_shouldChangeComment() {
        PostResponseDto postDto2 = postRepository.getPostById(2);
        Comment comment = postDto2.getCommentsList().getFirst();
        //comment.setText("Changed comment text");
        String changedText = "Changed comment text";
        commentRepository.changeComment(comment.getId(), postDto2.getId(), changedText);
        PostResponseDto postDto2WithChangedComment = postRepository.getPostById(postDto2.getId());
        assertTrue(postDto2WithChangedComment != null,
                "After changing comment post doesn't exist anymore");
        assertEquals(postDto2WithChangedComment.getCommentsList().get(0).getText(), changedText,
                "Text was changed incorrectly");
    }

    @Test
    void deleteComment_shouldDeleteComment() {
        PostResponseDto postDto1 = postRepository.getPostById(1);
        Comment comment = new Comment(1, 1, "Comment to Post1");
        postDto1.getCommentsList().add(comment);
        assertTrue(postDto1.getCommentsList().contains(comment));
        commentRepository.deleteComment(postDto1.getId(), comment.getId());
        PostResponseDto postDto1WithoutComment = postRepository.getPostById(postDto1.getId());;
        assertFalse(postDto1WithoutComment.getCommentsList().contains(comment),
                "Post 1 comments list shouldn't contain comment 1");
        assertTrue(postDto1WithoutComment.getCommentsList().isEmpty(), "Post 1 comments list should be empty");
    }
}
