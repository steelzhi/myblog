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
        String tagsString = "#Тег1, тег2, #тег3";
        PostDto postDto = new PostDto(1, "Название", null, "Текст", 0, tagsString);
        assertTrue(postDto.getTagsTextList().size() == 3, "В списке должно быть 3 тега");
        assertEquals(postDto.getTagsTextList().get(0), "#Тег1", "Теги не совпадают");
        assertEquals(postDto.getTagsTextList().get(1), "#тег2", "Теги не совпадают");
        assertEquals(postDto.getTagsTextList().get(2), "#тег3", "Теги не совпадают");
    }

    @Test
    void testCorrectPreview() {
        PostDto postDto1 = new PostDto(0, "Пост", null, "ТекстТекстТекст", 0,"#Тег1");
        assertTrue(postDto1.getPreview().length() <= PostDto.TEXT_MAX_LENGTH_FOR_PREVIEW, "Длина превью не должна превышать установленный предел");
        assertTrue(postDto1.getPreview().equals(postDto1.getText().substring(0, PostDto.TEXT_MAX_LENGTH_FOR_PREVIEW)), "Длина превью не должна превышать установленный предел");
    }
}
