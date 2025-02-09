package ru.yandex.practicum.mapper;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Post;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
public class PostMapperTest {
    @Test
    void testCorrectMapping() throws IOException {
        Post post = new Post(0, "Пост № 1", null, "1-й пост", "#Тег1, Тег2");
        PostDto postDto = PostMapper.mapToPostDto(post);
        assertEquals(postDto.getId(), 0, "Id должен быть 0");
        assertEquals(postDto.getName(), post.getName(), "Названия постов должны совпадать");
        assertEquals(postDto.getBase64Image(), null, "Картинки у поста не было");
        assertEquals(postDto.getText(), post.getText(), "Тексты постов должны совпадать");

        for (String tagText : postDto.getTagsTextList()) {
            String tagTextWithNoCage = tagText;
            if (tagText.startsWith("#")) {
                tagTextWithNoCage = tagText.substring(1, tagText.length());
            }
            assertTrue(post.getTagsString().contains(tagTextWithNoCage), "Неправильно распарсены теги");
        }
    }
}
