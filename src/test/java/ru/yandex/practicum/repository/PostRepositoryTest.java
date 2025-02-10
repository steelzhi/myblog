package ru.yandex.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.WebConfiguration;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.dto.PostDto;

import java.util.List;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, JdbcBlogRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostRepositoryTest {

/*    RowMapper<PostDto> MAP_TO_POSTDTO = (ResultSet resultSet, int rowNum) -> new PostDto(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getString("base_64_image"),
            resultSet.getString("text"),
            resultSet.getInt("number_of_likes"),
            null);

                    List<PostDto> list = jdbcTemplate.query("SELECT * FROM posts", MAP_TO_POSTDTO);
        System.out.println();*/

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
    }
}
