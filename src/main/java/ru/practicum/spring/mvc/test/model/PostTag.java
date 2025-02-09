package ru.practicum.spring.mvc.test.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostTag {
    private int id;
    private int postId;
    private int tagId;
}
