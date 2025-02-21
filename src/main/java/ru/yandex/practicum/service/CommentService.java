package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    public PostResponseDto addComment(int postId, Comment comment) {
        commentRepository.addComment(postId, comment.getText());
        return postRepository.getPostById(postId);
    }

    public PostResponseDto changeComment(int id, int postId, String text) {
        commentRepository.changeComment(id, postId, text);
        return postRepository.getPostById(postId);
    }

    public PostResponseDto deleteComment(int postDtoId, int commentId) {
        commentRepository.deleteComment(postDtoId, commentId);
        return postRepository.getPostById(postDtoId);
    }
}
