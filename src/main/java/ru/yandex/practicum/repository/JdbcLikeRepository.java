package ru.yandex.practicum.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.PostResponseDto;

import java.sql.ResultSet;

@Repository
public class JdbcLikeRepository implements LikeRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int postId) {
        jdbcTemplate.update("""
                        UPDATE posts 
                        SET number_of_likes = number_of_likes + 1
                        WHERE id = ?
                        """,
                postId
        );
    }
}
