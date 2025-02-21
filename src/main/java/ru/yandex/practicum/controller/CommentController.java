package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Pages;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.PostService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/feed")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/post/{id}/addComment")
    public String addComment(@PathVariable(name = "id") int id, @ModelAttribute Comment comment) {
        commentService.addComment(id, comment);

        return "redirect:/feed/post/" + id;
    }

    @PostMapping(value = "/post/comment")
    public String changeComment(@RequestParam(name = "id") int id,
                                @RequestParam(name = "postId") int postId,
                                @RequestParam(name = "text") String text) {
        commentService.changeComment(id, postId, text);
        return "redirect:/feed/post/" + postId;
    }

    @PostMapping(value = "/post/{id}/removeComment/{commentId}", params = "_method=delete")
    public String deleteComment(@PathVariable(name = "id") int id,
                                @PathVariable(name = "commentId") int commentId) {
        commentService.deleteComment(id, commentId);

        return "redirect:/feed/post/" + id;
    }
}