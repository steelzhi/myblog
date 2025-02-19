package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.dto.PostRequestDto;

import java.io.*;

public class PostMapper {
    private PostMapper() {
    }

    public static PostRequestDto mapToPostRequestDto(Post post) throws IOException {
        byte[] blob = null;
        if (post.getFile() != null) {
            blob = post.getFile().getBytes();
        }

        return new PostRequestDto(
                post.getId(),
                post.getName(),
                blob,
                post.getText(),
                post.getTagsString());
    }

/*    public static PostResponseDto mapToPostResponseDto(PostRequestDto postRequestDto) throws IOException {
        byte[] blob = null;
        if (post.getFile() != null) {
            blob = post.getFile().getBytes();
        }

        return new PostRequestDto(
                post.getId(),
                post.getName(),
                blob,
                post.getText(),
                post.getTagsString());
    }*/
}
