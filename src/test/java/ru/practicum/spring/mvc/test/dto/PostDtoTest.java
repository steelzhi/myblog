package ru.practicum.spring.mvc.test.dto;

/*import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;*/

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostDtoTest {
    //@ParametirezedTest

    @Test
    void testCorrectMappingTagsFromStringToList() {
        String tagsString = "#���1, ���2, #���3";
        PostDto postDto = new PostDto(1, "��������", null, "�����", 0, tagsString);
        assertTrue(postDto.getTagsTextList().size() == 3, "� ������ ������ ���� 3 ����");
        assertEquals(postDto.getTagsTextList().get(0), "#���1", "���� �� ���������");
        assertEquals(postDto.getTagsTextList().get(1), "#���2", "���� �� ���������");
        assertEquals(postDto.getTagsTextList().get(2), "#���3", "���� �� ���������");
    }

    @Test
    void testCorrectPreview() {
        PostDto postDto1 = new PostDto(0, "����", null, "���������������", 0,"#���1");
        assertTrue(postDto1.getPreview().length() <= PostDto.TEXT_MAX_LENGTH_FOR_PREVIEW, "����� ������ �� ������ ��������� ������������� ������");
        assertTrue(postDto1.getPreview().equals(postDto1.getText().substring(0, PostDto.TEXT_MAX_LENGTH_FOR_PREVIEW)), "����� ������ �� ������ ��������� ������������� ������");
    }
}
