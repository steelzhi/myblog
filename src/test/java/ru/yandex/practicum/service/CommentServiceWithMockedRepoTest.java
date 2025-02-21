package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = CommentService.class)
public class CommentServiceWithMockedRepoTest {
    @Autowired
    CommentService commentService;

    @MockitoBean
    PostRepository postRepository;

    @MockitoBean
    CommentRepository commentRepository;

    public CommentServiceWithMockedRepoTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(postRepository);
    }

    @Test
    void testAddComment() {
        PostResponseDto mockPostDtoWithoutComment
                = new PostResponseDto(1, "Post1", null, "Text1", 1, "#Tag1");
        Comment comment = new Comment(0, 1, "Comment");
        Mockito.when(commentRepository.addComment(mockPostDtoWithoutComment.getId(), comment.getText()))
                        .thenReturn(comment);
        Comment addedComment = commentRepository.addComment(mockPostDtoWithoutComment.getId(), comment.getText());
        PostResponseDto mockPostDtoWithComment = new PostResponseDto(
                mockPostDtoWithoutComment.getId(),
                mockPostDtoWithoutComment.getName(),
                mockPostDtoWithoutComment.getImage(),
                mockPostDtoWithoutComment.getText(),
                mockPostDtoWithoutComment.getNumberOfLikes(),
                "#Tag1"
        );
        mockPostDtoWithComment.getCommentsList().add(addedComment);

        Mockito.when(postRepository.getPostById(mockPostDtoWithComment.getId()))
                .thenReturn(mockPostDtoWithComment);

        PostResponseDto postDtoWithComment = commentService.addComment(1, comment);
        assertTrue(postDtoWithComment != null, "Post wasn't added");
        assertTrue(postDtoWithComment.getCommentsList().contains(comment), "Comment comment);\n" +
                "        assertTrue(postDtoWithComment != null, \"Post wasn't added");

        Mockito.verify(commentRepository, times(2)).addComment(mockPostDtoWithoutComment.getId(), comment.getText());
        Mockito.verify(postRepository, times(1)).getPostById(mockPostDtoWithComment.getId());
    }

    @Test
    void changeComment() {
        PostResponseDto mockPostDto
                = new PostResponseDto(10, "Post 10", null, "Text 10", 0, "Tag10");
        Comment comment = new Comment(10, mockPostDto.getId(), "Comment 10");
        mockPostDto.getCommentsList().add(comment);
        String changedText = "Changed text 10";

        Mockito.when(commentRepository.changeComment(comment.getId(), mockPostDto.getId(), changedText))
                .thenReturn(comment);
        Comment changedComment = commentRepository.changeComment(comment.getId(), mockPostDto.getId(), changedText);
        PostResponseDto mockPostDtoWithComment = new PostResponseDto(
                mockPostDto.getId(),
                mockPostDto.getName(),
                mockPostDto.getImage(),
                mockPostDto.getText(),
                mockPostDto.getNumberOfLikes(),
                "#Tag10"
        );
        mockPostDtoWithComment.getCommentsList().add(changedComment);

        Mockito.when(postRepository.getPostById(mockPostDtoWithComment.getId()))
                .thenReturn(mockPostDtoWithComment);

        PostResponseDto postDtoWithChangedComment = commentService.addComment(mockPostDto.getId(), comment);
        assertTrue(postDtoWithChangedComment != null, "Feed doesn't contain post");
        assertEquals(postDtoWithChangedComment, mockPostDto, "Post wasn't changed or was changed incorrectly");

        Mockito.verify(commentRepository, times(1))
                .changeComment(mockPostDto.getId(), comment.getId(), changedText);
        Mockito.verify(postRepository, times(1)).getPostById(mockPostDtoWithComment.getId());

    }

    @Test
    void testDeleteComment() {
        PostResponseDto mockPostDtoWithComment
                = new PostResponseDto(1, "Post1", null, "Text1", 0, "#Tag1");
        Comment comment = new Comment(1, 1, "Comment");
        mockPostDtoWithComment.getCommentsList().add(comment);
        PostResponseDto mockPostDtoWithoutComment
                = new PostResponseDto(1, "Post1", null, "Text1", 0, "#Tag1");
        Mockito.when(postRepository.getPostById(mockPostDtoWithComment.getId()))
                .thenReturn(mockPostDtoWithoutComment);
        PostResponseDto postDtoWithoutComment
                = commentService.deleteComment(mockPostDtoWithComment.getId(), comment.getId());
        Mockito.verify(commentRepository, times(1))
                .deleteComment(mockPostDtoWithComment.getId(), comment.getId());

        assertTrue(postDtoWithoutComment != null, "Post should exist after comment deleting");
        assertTrue(postDtoWithoutComment.getCommentsList().isEmpty(), "Comments list should be empty");

        Mockito.verify(postRepository, times(1)).getPostById(mockPostDtoWithComment.getId());
    }
}
