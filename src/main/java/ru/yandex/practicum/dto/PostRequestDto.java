package ru.yandex.practicum.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.model.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PostRequestDto {
    private int id;
    private String name;
    private byte[] image;
    private String text;
    private List<String> tagsTextList;

    public PostRequestDto(int id, String name, byte[] image, String text, String tagsString) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.text = text;
        tagsTextList = mapTagsFromStringAndAddThemToList(tagsString);
    }

    public List<String> mapTagsFromStringAndAddThemToList(String tagsString) {
        List<String> tags = new ArrayList<>();
        if (tagsString != null) {
            String[] tagsTextArray = tagsString.replaceAll(" ", "").split(",");
            for (String tagText : tagsTextArray) {
                if (tagText.startsWith("#")) {
                    tags.add(tagText);

                } else {
                    tags.add("#" + tagText);
                }
            }
        }

        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PostRequestDto postDto = (PostRequestDto) o;
        return id == postDto.id
                && Objects.equals(name, postDto.name)
                && Objects.equals(image, postDto.image)
                && Objects.equals(text, postDto.text)
                && Objects.equals(tagsTextList, postDto.tagsTextList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image, text, tagsTextList);
    }
}
