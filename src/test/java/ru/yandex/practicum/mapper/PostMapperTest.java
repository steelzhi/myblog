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
        Post post = new Post(0, "Post 1", null, "First post", "#Tag1, Tag2");
        PostDto postDto = PostMapper.mapToPostDto(post);
        assertEquals(postDto.getId(), 0, "Id should be 0");
        assertEquals(postDto.getName(), post.getName(), "Posts names should be the same");
        assertEquals(postDto.getBase64Image(), null, "The post didn't have an image");
        assertEquals(postDto.getText(), post.getText(), "Posts texts should be the same");

        for (String tagText : postDto.getTagsTextList()) {
            String tagTextWithNoCage = tagText;
            if (tagText.startsWith("#")) {
                tagTextWithNoCage = tagText.substring(1, tagText.length());
            }
            assertTrue(post.getTagsString().contains(tagTextWithNoCage), "Tags were parsed incorrect");
        }
    }
}
