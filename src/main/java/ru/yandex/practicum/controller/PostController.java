package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/feed")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public String getFeed(Model model) {
        List<PostDao> feed = postService.getSortedFeed();
        model.addAttribute("feed", feed);

        return "feed";
    }

    @GetMapping("/tags/")
    public String getFeedWithChosenTags(@RequestParam(name = "tagsString") String tagsString, Model model) {
        List<PostDao> feedWithChosenTags = postService.getFeedWithChosenTags(tagsString);
        model.addAttribute("feed", feedWithChosenTags);

        return "feed";
    }

    @PostMapping
    public String addPost(@ModelAttribute Post post) throws IOException {
        postService.addPost(post);
        return "redirect:/feed";
    }

    @PostMapping("/post/change/{id}")
    public String changePost(@ModelAttribute Post post, @PathVariable(name = "id") Integer id) throws IOException {
        post.setId(id);
        postService.changePost(post);

        return "redirect:/feed/post/" + id;
    }

    @PostMapping("/post/addLike/{id}")
    public String addLike(@PathVariable(name = "id") Integer id) throws IOException {
        postService.addLike(id);
        PostDao postDao = postService.getPostById(Long.valueOf(id));

        return "redirect:/feed/post/" + id;
    }

    @PostMapping(value = "/{id}", params = "_method=delete")
    public String delete(@PathVariable(name = "id") Long id) {
        postService.deletePost(id);
        List<PostDao> feed = postService.getSortedFeed();

        return "redirect:/feed";
    }

    @GetMapping("/post/{id}")
    public String getPostById(@PathVariable(name = "id") Long id, Model model) {
        PostDao postDao = postService.getPostById(id);
        model.addAttribute("postDao", postDao);

        return "post";
    }
}