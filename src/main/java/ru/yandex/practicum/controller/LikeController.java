package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.service.LikeService;

@Controller
@RequestMapping("/feed")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping("/post/{id}/addLike")
    public String addLike(@PathVariable(name = "id") int id) {
        likeService.addLike(id);

        return "redirect:/feed/post/" + id;
    }
}