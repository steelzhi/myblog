package ru.yandex.practicum.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.repository.PostRepository;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test-application.properties")
@Sql("classpath:test-schema.sql")
public class ImageControllerAllLayersTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    public void setUp() {
        // Очистка базы данных перед каждым тестом
        postRepository.cleanAllDataBase();
    }

    @Test
    void getImage_shouldReturnImage() throws Exception {
        byte[] image = Files.readAllBytes(Paths.get("src/test/resources/image-byte-array.txt"));
        PostDto postDto
                = new PostDto(1, "Post1", image, "Text1", 1, "#Tag1");

        postRepository.addPostDto(postDto);

        mockMvc.perform(get("/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"));

        PostDto postDtoFromDb = postRepository.getPostById(1);
        byte[] imageFromDb = postDtoFromDb.getImage();
        assertArrayEquals(image, imageFromDb, "Image was saved or retrieved incorrectly");
    }
}
