package ru.yandex.practicum.repository;

import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;

import java.sql.SQLException;
import java.util.List;

public interface CommentRepository {

    Comment addComment(int postId, String commentText);

    Comment changeComment(int id, int postId, String text);

    void deleteComment(int postDtoId, int commentId);
}
