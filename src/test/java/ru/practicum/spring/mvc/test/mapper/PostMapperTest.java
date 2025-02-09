package ru.practicum.spring.mvc.test.mapper;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.practicum.spring.mvc.test.dto.PostDto;
import ru.practicum.spring.mvc.test.model.Post;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
public class PostMapperTest {
    @Test
    void testCorrectMapping() throws IOException {
        Post post = new Post(0, "���� � 1", null, "1-� ����", "#���1, ���2");
        PostDto postDto = PostMapper.mapToPostDto(post);
        assertEquals(postDto.getId(), 0, "Id ������ ���� 0");
        assertEquals(postDto.getName(), post.getName(), "�������� ������ ������ ���������");
        assertEquals(postDto.getBase64Image(), null, "�������� � ����� �� ����");
        assertEquals(postDto.getText(), post.getText(), "������ ������ ������ ���������");

        for (String tagText : postDto.getTagsTextList()) {
            String tagTextWithNoCage = tagText;
            if (tagText.startsWith("#")) {
                tagTextWithNoCage = tagText.substring(1, tagText.length());
            }
            assertTrue(post.getTagsString().contains(tagTextWithNoCage), "����������� ���������� ����");
        }
    }
}
