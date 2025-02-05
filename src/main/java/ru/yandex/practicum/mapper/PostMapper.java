package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.model.Post;

import java.io.*;

public class PostMapper {
    private PostMapper() {
    }

    public static PostDao mapToPostDao(Post post) throws IOException {
/*        byte[] bytes = post.getFile().getBytes();
        String imageBase64 = Base64.getEncoder().encodeToString(bytes);*/

        return new PostDao(
                0,
                post.getName(),
                /*imageBase64,*/
                post.getText(),
                0,
                post.getTags());
    }
}
