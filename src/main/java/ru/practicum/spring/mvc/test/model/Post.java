package ru.practicum.spring.mvc.test.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private int id;
    private String name;
    private MultipartFile file;
    private String text;
    private String tagsString;
}
