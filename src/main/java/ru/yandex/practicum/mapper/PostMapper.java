package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.dto.PostDto;

import java.io.*;
import java.util.Base64;

public class PostMapper {
    private PostMapper() {
    }

    public static PostDto mapToPostDto(Post post) throws IOException {
        byte[] bytes = post.getFile().getBytes();
        String imageBase64 = Base64.getEncoder().encodeToString(bytes);

        return new PostDto(
                post.getId(),
                post.getName(),
                imageBase64,
                post.getText(),
                0,
                post.getTagsString());
    }
}
