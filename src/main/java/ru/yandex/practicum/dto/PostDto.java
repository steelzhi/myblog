package ru.yandex.practicum.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.model.Comment;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> tagsTextList = new ArrayList<>();
    private List<Comment> commentsList = new ArrayList<>();
    private static final int TEXT_MAX_LENGTH_FOR_PREVIEW = 5;

    public PostDto(int id, String name, String base64Image, String text, int numberOfLikes, String tagsString) {
        this.id = id;
        this.name = name;
        this.base64Image = base64Image;
        this.text = text;
        this.numberOfLikes = numberOfLikes;

        if (tagsString != null) {
            String[] tagsTextArray = tagsString.replaceAll(" ", "").split(",");
            for (String tagText : tagsTextArray) {
                if (tagText.startsWith("#")) {
                    tagsTextList.add(tagText);

                } else {
                    tagsTextList.add("#" + tagText);
                }
            }
        }

        this.preview = getPreview();
    }

    public String getPreview() {
        int posOfFirstParagraphEnd = text.contains("\n") ? text.indexOf("\n") : (text.length() - 1);
        if (posOfFirstParagraphEnd <= TEXT_MAX_LENGTH_FOR_PREVIEW) {
            return text.substring(0, posOfFirstParagraphEnd + 1);
        }

        return text.substring(0, TEXT_MAX_LENGTH_FOR_PREVIEW);
    }
}
