package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.Comment;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.dao.PostDao;

import java.sql.ResultSet;
import java.util.List;

public interface PostRepository {
    RowMapper<PostDao> MAP_TO_POST = (ResultSet resultSet, int rowNum) -> new PostDao(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            /*            resultSet.getString("base_64_image"),*/
            resultSet.getString("text"),
            resultSet.getInt("number_of_likes"),
            resultSet.getString("tags"));

    RowMapper<Comment> MAP_TO_COMMENTS = (ResultSet resultSet, int rowNum) -> new Comment(
            resultSet.getInt("post_id"),
            resultSet.getString("text"));

    RowMapper<String> MAP_TO_COMMENT = (ResultSet resultSet, int rowNum) -> new String(
            resultSet.getString("text"));

    void addPost(PostDao postDao);

    List<PostDao> getSortedFeed();

    void deletePost(Long id);

    PostDao getPostById(Long id);

}
