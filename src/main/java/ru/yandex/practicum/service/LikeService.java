package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.JdbcPostRepository;
import ru.yandex.practicum.repository.LikeRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
@AllArgsConstructor
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    public PostResponseDto addLike(int id) {
        likeRepository.addLike(id);
        return postRepository.getPostById(id);
    }
}
