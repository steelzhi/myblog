package ru.yandex.practicum.repository;

import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;

import java.sql.SQLException;
import java.util.List;

public interface PostRepository {
    PostResponseDto addPostDto(PostRequestDto postDto);

    List<PostResponseDto> getSortedFeed();

    List<PostResponseDto> getFeedWithChosenTags(String tagsString) throws SQLException;

    PostResponseDto getPostById(int id);

    List<PostResponseDto> getFeedSplittedByPages(int postsOnPage, int pageNumber);

    PostResponseDto changePost(PostRequestDto newPostDto);

    void deletePost(int id);

    void cleanAllPosts();
}
