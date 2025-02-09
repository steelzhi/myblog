package ru.practicum.spring.mvc.test.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pages {
    private int postsOnPage;
    private int numberOfPages;
}
