package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.dto.PostDto;

import java.io.*;
import java.util.Base64;

public class PostMapper {
    private PostMapper() {
    }

    public static PostDto mapToPostDto(Post post) throws IOException {
        byte[] blob = null;
        if (post.getFile() != null) {
            blob = post.getFile().getBytes();
        }

        return new PostDto(
                post.getId(),
                post.getName(),
                blob,
                post.getText(),
                0,
                post.getTagsString());
    }
}
