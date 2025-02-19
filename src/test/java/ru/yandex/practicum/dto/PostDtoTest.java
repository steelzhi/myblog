package ru.yandex.practicum.dto;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostDtoTest {

    @Test
    void testCorrectMappingTagsFromStringToList() {
        String tagsString = "#Tag1, tag2, #tag3";
        PostRequestDto postDto = new PostRequestDto(1, "Name", null, "Text", tagsString);
        assertTrue(postDto.getTagsTextList().size() == 3, "List should contain 3 tags");
        assertEquals(postDto.getTagsTextList().get(0), "#Tag1", "Tags do not match");
        assertEquals(postDto.getTagsTextList().get(1), "#tag2", "Tags do not match");
        assertEquals(postDto.getTagsTextList().get(2), "#tag3", "Tags do not match");
    }

    @Test
    void testCorrectPreview() {
        PostResponseDto postDto1 = new PostResponseDto(
                0, "Post", null, "TextTextText", 0,"#Tag1");
        assertTrue(postDto1.getPreview().length() <= PostResponseDto.TEXT_MAX_LENGTH_FOR_PREVIEW,
                "Preview length should not exceed limit length");
        assertTrue(postDto1.getPreview().equals(postDto1.getText().substring(0, PostResponseDto.TEXT_MAX_LENGTH_FOR_PREVIEW)),
                "Preview length should not exceed limit length");
    }
}
