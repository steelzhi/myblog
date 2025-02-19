package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.yandex.practicum.service.PostService;

@Controller
public class ImageController {
    @Autowired
    private PostService postService;

    @ResponseBody
    @GetMapping("/{postDtoId}/image")
    public byte[] getImage(@PathVariable(name = "postDtoId") int postDtoId) {
        return postService.getImage(postDtoId);
    }

}
