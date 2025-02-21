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
import ru.yandex.practicum.repository.LikeRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = LikeService.class)
public class LikeServiceWithMockedRepoTest {
    @Autowired
    LikeService likeService;

    @MockitoBean
    PostRepository postRepository;

    @MockitoBean
    LikeRepository likeRepository;

    public LikeServiceWithMockedRepoTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(postRepository);
    }

    @Test
    void testAddLike() {
        PostResponseDto mockPostDto1WithLike
                = new PostResponseDto(1, "Post1", null, "Text1", 1, "#Tag1");

        Mockito.when(postRepository.getPostById(mockPostDto1WithLike.getId()))
                .thenReturn(mockPostDto1WithLike);
        PostResponseDto postDtoWithLike = likeService.addLike(mockPostDto1WithLike.getId());

        assertTrue(postDtoWithLike != null, "Post wasn't added");
        assertTrue(postDtoWithLike.getNumberOfLikes() == 1, "Like wasn't added to post");

        Mockito.verify(likeRepository, times(1)).addLike(mockPostDto1WithLike.getId());
        Mockito.verify(postRepository, times(1)).getPostById(mockPostDto1WithLike.getId());
    }
}
