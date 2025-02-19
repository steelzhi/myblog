package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.service.PostService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest
public class ImageControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    private static byte[] image;

    @BeforeAll
    static void getImage() throws IOException {
        image = Files.readAllBytes(Paths.get("src/test/resources/image-byte-array.txt"));
    }

    @Test
    void getImage_shouldReturnImage() throws Exception {
        when(postService.getImage(1))
                .thenReturn(image);

        mockMvc.perform(get("/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"));

        verify(postService, times(1)).getImage(1);
    }
}
