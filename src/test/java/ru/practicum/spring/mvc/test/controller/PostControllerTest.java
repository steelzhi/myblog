/*
package ru.practicum.spring.mvc.test.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.spring.mvc.test.dto.PostDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {PostController.class})
@WebAppConfiguration
public class PostControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PostController()).build();
    }

    @Test
    void getPosts_shouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("feed"))
                .andExpect(xpath("//table/tbody/tr").nodeCount(3))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").string("1"));
    }
}

@RestController
@RequestMapping(value = "/feed", produces = "text/html;charset=UTF-8")
class PostController {

    @GetMapping
    public String getFeed(Model model) {
        PostDto postDto = new PostDto(1, "Пост", null, "Текст", 0, "#Тег1");
        model.addAttribute("feed", List.of(postDto));

        return "feed";
    }
}
*/
