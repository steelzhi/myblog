package ru.practicum.spring.mvc.test.repository;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.spring.mvc.test.WebConfiguration;
import ru.practicum.spring.mvc.test.configuration.DataSourceConfiguration;
import ru.practicum.spring.mvc.test.dto.PostDto;

import java.sql.ResultSet;
import java.util.List;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostRepositoryTest {

    RowMapper<PostDto> MAP_TO_POSTDTO = (ResultSet resultSet, int rowNum) -> new PostDto(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getString("base_64_image"),
            resultSet.getString("text"),
            resultSet.getInt("number_of_likes"),
            null);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        // Очистка и заполнение тестовых данных в базе
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM tags");
        jdbcTemplate.execute("DELETE FROM posts_tags");

        jdbcTemplate.execute("INSERT INTO posts (name, text) VALUES ('1', '1');");
        List<PostDto> list = jdbcTemplate.query("SELECT * FROM posts", MAP_TO_POSTDTO);
        System.out.println();
/*        jdbcTemplate.execute("INSERT INTO posts (name, text) VALUES ('2-й пост', 'Это 2-й пост');");
        jdbcTemplate.execute("INSERT INTO posts (name, text) VALUES ('3-й пост', 'Это 3-й пост');");
        jdbcTemplate.execute("INSERT INTO comments (text, post_id) VALUES ('Это самый 1-й пост!', 1);");
        jdbcTemplate.execute("INSERT INTO comments (text, post_id) VALUES ('Это самый 1-й пост!. И - 2-й комментарий к нему', 1);");
        jdbcTemplate.execute("INSERT INTO comments (text, post_id) VALUES ('Это 3-й пост!.', 3);");
        jdbcTemplate.execute("INSERT INTO tags (id, text) VALUES (1, '#Общий тег');");
        jdbcTemplate.execute("INSERT INTO tags (id, text) VALUES (2, '#Тег для поста 2');");
        jdbcTemplate.execute("INSERT INTO tags (id, text) VALUES (3, '#Тег для поста 3');");
        jdbcTemplate.execute("INSERT INTO posts_tags (post_id, tag_id) VALUES (1, 1);");
        jdbcTemplate.execute("INSERT INTO posts_tags (post_id, tag_id) VALUES (2, 1);");
        jdbcTemplate.execute("INSERT INTO posts_tags (post_id, tag_id) VALUES (2, 2);");
        jdbcTemplate.execute("INSERT INTO posts_tags (post_id, tag_id) VALUES (3, 1);");
        jdbcTemplate.execute("INSERT INTO posts_tags (post_id, tag_id) VALUES (3, 3);");*/
    }
}