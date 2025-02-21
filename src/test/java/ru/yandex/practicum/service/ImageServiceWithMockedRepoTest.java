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
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = ImageService.class)
public class ImageServiceWithMockedRepoTest {
    @Autowired
    ImageService imageService;

    @MockitoBean
    PostRepository postRepository;

    @MockitoBean
    ImageRepository imageRepository;

    public ImageServiceWithMockedRepoTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(postRepository);
    }

    @Test
    void testGetImage() throws IOException {
        byte[] image = Files.readAllBytes(Paths.get("src/test/resources/image-byte-array.txt"));
        Mockito.when(imageRepository.getImage(1))
                .thenReturn(image);

        byte[] imageFromDb = imageService.getImage(1);
        assertNotNull(imageFromDb, "Post should exist");
        assertArrayEquals(imageFromDb, imageFromDb, "Image was saved or retrieved incorrectly");

        Mockito.verify(imageRepository, times(1)).getImage(1);
    }
}
