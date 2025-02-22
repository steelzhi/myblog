package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.model.Post;

import java.io.IOException;

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
}
