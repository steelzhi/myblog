package ru.practicum.spring.mvc.test.mapper;

import ru.practicum.spring.mvc.test.model.Post;
import ru.practicum.spring.mvc.test.dto.PostDto;

import java.io.*;
import java.util.Base64;

public class PostMapper {
    private PostMapper() {
    }

    public static PostDto mapToPostDto(Post post) throws IOException {
        String imageBase64 = null;
        if (post.getFile() != null) {
            byte[] bytes = post.getFile().getBytes();
            imageBase64 = Base64.getEncoder().encodeToString(bytes);
        }

        return new PostDto(
                post.getId(),
                post.getName(),
                imageBase64,
                post.getText(),
                0,
                post.getTagsString());
    }
}
