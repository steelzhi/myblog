package ru.yandex.practicum.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostDao {
    private int id;
    private String name;
    /*    private String base64Image;*/
    private String preview;
    private String text;
    private int numberOfLikes;
    private String tags;
    private List<String> comments = new ArrayList<>();
    private static final int TEXT_MAX_LENGTH_FOR_PREVIEW = 5;

    public PostDao(int id, String name, /*String base64Image,*/ String text, int numberOfLikes, String tags) {
        this.id = id;
        this.name = name;
        /*        this.base64Image = base64Image;*/
        this.text = text;
        this.numberOfLikes = 0;
        this.tags = tags;
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
