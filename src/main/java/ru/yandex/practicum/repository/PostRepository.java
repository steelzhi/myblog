package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.Comment;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.model.PostTag;
import ru.yandex.practicum.model.Tag;

import java.sql.ResultSet;
import java.util.List;

public interface PostRepository {
    RowMapper<PostDao> MAP_TO_POSTDAO = (ResultSet resultSet, int rowNum) -> new PostDao(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getString("base_64_image"),
            resultSet.getString("text"),
            resultSet.getInt("number_of_likes"),
            null);

    RowMapper<Comment> MAP_TO_COMMENTS = (ResultSet resultSet, int rowNum) -> new Comment(
            resultSet.getInt("post_id"),
            resultSet.getString("text"));

    RowMapper<Tag> MAP_TO_TAG = (ResultSet resultSet, int rowNum) -> new Tag(
            resultSet.getInt("id"),
            resultSet.getString("text"));

    RowMapper<Integer> MAP_TO_ID = (ResultSet resultSet, int rowNum) -> new Integer(
            resultSet.getInt("id"));

    RowMapper<Integer> MAP_TO_TAG_ID = (ResultSet resultSet, int rowNum) -> new Integer(
            resultSet.getInt("tag_id"));

    RowMapper<Integer> MAP_TO_POST_ID = (ResultSet resultSet, int rowNum) -> new Integer(
            resultSet.getInt("post_id"));

    RowMapper<String> MAP_TO_TEXT = (ResultSet resultSet, int rowNum) -> new String(
            resultSet.getString("text"));

    RowMapper<PostTag> MAP_TO_POST_TAG = (ResultSet resultSet, int rowNum) -> new PostTag(
            resultSet.getInt("id"),
            resultSet.getInt("post_id"),
            resultSet.getInt("tag_id"));

    void addPostDao(PostDao postDao);

    List<PostDao> getSortedFeed();

    public List<PostDao> getFeedWithChosenTags(String tagsString);

    void deletePost(Long id);

    PostDao getPostById(Long id);

    void changePost(PostDao newPostDao);

    void addLike(int postDaoId);

    /*    public List<PostDao> findByTags(String[] tags);*/

}
