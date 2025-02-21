package ru.yandex.practicum.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.PostTag;
import ru.yandex.practicum.model.Tag;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class JdbcCommentRepository implements CommentRepository {
    private RowMapper<Comment> MAP_TO_COMMENT = (ResultSet resultSet, int rowNum) -> new Comment(
            resultSet.getInt("id"),
            resultSet.getInt("post_id"),
            resultSet.getString("text"));

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /*
    * Возвраты значений нужны в методах для проверки слоев сервисов в тестах
     */
    @Override
    public Comment addComment(int postId, String commentText) {
        jdbcTemplate.update("""
                INSERT INTO comments (post_id, text) 
                VALUES (?, ?)
                """, postId, commentText);

        PreparedStatementCreator psc = con -> {
            PreparedStatement selectComment = con.prepareStatement(
                    """
                SELECT id, post_id, text
                FROM comments
                WHERE post_id = ?
                    AND text = ?
                ORDER BY id DESC
                LIMIT (1)
                            """);
            selectComment.setInt(1, postId);
            selectComment.setString(2, commentText);

            return selectComment;
        };

        Comment comment = jdbcTemplate.query(psc, MAP_TO_COMMENT).getFirst();
        return comment;
    }

    @Override
    public Comment changeComment(int id, int postId, String text) {
        jdbcTemplate.update("""
                UPDATE comments 
                SET text = ? 
                WHERE id = ? 
                    AND post_id = ?
                """, text, id, postId);

        PreparedStatementCreator psc = con -> {
            PreparedStatement selectComment = con.prepareStatement(
                    """
                SELECT id, text, post_id
                FROM comments
                WHERE id = ?
                            """);
            selectComment.setInt(1, id);

            return selectComment;
        };

        Comment comment = jdbcTemplate.query(psc, MAP_TO_COMMENT).getFirst();
        return comment;
    }

    @Override
    public void deleteComment(int postDtoId, int commentId) {
        jdbcTemplate.update("""
                DELETE FROM comments
                WHERE id = ?
                """, commentId);
    }
}
