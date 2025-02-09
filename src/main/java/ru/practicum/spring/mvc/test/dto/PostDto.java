package ru.practicum.spring.mvc.test.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.spring.mvc.test.model.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PostDto {
    private int id;
    private String name;
    private String base64Image;
    private String preview;
    private String text;
    private int numberOfLikes;
    private List<String> tagsTextList;
    private List<Comment> commentsList = new ArrayList<>();
    public static final int TEXT_MAX_LENGTH_FOR_PREVIEW = 5;

    public PostDto(int id, String name, String base64Image, String text, int numberOfLikes, String tagsString) {
        this.id = id;
        this.name = name;
        this.base64Image = base64Image;
        this.text = text;
        this.numberOfLikes = numberOfLikes;
        tagsTextList = mapTagsFromStringAndAddThemToList(tagsString);
        this.preview = getPreview();
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

    public String getPreview() {
        int posOfFirstParagraphEnd = text.contains("\n") ? text.indexOf("\n") : (text.length() - 1);
        if (posOfFirstParagraphEnd <= TEXT_MAX_LENGTH_FOR_PREVIEW) {
            return text.substring(0, posOfFirstParagraphEnd + 1);
        }

        return text.substring(0, TEXT_MAX_LENGTH_FOR_PREVIEW);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PostDto postDto = (PostDto) o;
        return id == postDto.id && numberOfLikes == postDto.numberOfLikes && Objects.equals(name, postDto.name) && Objects.equals(base64Image, postDto.base64Image) && Objects.equals(preview, postDto.preview) && Objects.equals(text, postDto.text) && Objects.equals(tagsTextList, postDto.tagsTextList) && Objects.equals(commentsList, postDto.commentsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, base64Image, preview, text, numberOfLikes, tagsTextList, commentsList);
    }
}
