package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.Comment;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.PostTag;
import ru.yandex.practicum.model.Tag;

import java.sql.ResultSet;
import java.util.List;

public interface PostRepository {
    PostDto addPostDto(PostDto postDto);

    PostDto addLike(int postDtoId);

    PostDto addComment(int postId, String commentText);

    List<PostDto> getSortedFeed();

    List<PostDto> getFeedWithChosenTags(String tagsString);

    PostDto getPostById(int id);

    List<PostDto> getFeedSplittedByPages(int postsOnPage, int pageNumber);

    PostDto changePost(PostDto newPostDto);

    PostDto changeComment(int id, int postId, String text);

    void deletePost(int id);

    PostDto deleteComment(int postDtoId, int commentId);

    byte[] getImage(int postDtoId);

    void cleanAllDataBase();
}
