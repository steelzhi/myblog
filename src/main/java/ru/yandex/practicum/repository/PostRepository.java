package ru.yandex.practicum.repository;

import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;

import java.sql.SQLException;
import java.util.List;

public interface PostRepository {
    PostResponseDto addPostDto(PostRequestDto postDto);

    PostResponseDto addLike(int postDtoId);

    PostResponseDto addComment(int postId, String commentText);

    List<PostResponseDto> getSortedFeed();

    List<PostResponseDto> getFeedWithChosenTags(String tagsString) throws SQLException;

    PostResponseDto getPostById(int id);

    List<PostResponseDto> getFeedSplittedByPages(int postsOnPage, int pageNumber);

    PostResponseDto changePost(PostRequestDto newPostDto);

    PostResponseDto changeComment(int id, int postId, String text);

    void deletePost(int id);

    PostResponseDto deleteComment(int postDtoId, int commentId);

    byte[] getImage(int postDtoId);

    void cleanAllDataBase();
}
