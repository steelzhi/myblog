package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Pages;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/feed")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping
    public String addPost(@ModelAttribute Post post) throws IOException {
        postService.addPost(post);
        return "redirect:/feed";
    }

    @PostMapping("/post/{id}/addLike")
    public String addLike(@PathVariable(name = "id") int id) throws IOException {
        postService.addLike(id);

        return "redirect:/feed/post/" + id;
    }

    @PostMapping("/post/{id}/addComment")
    public String addComment(@PathVariable(name = "id") int id, @ModelAttribute Comment comment) {
        postService.addComment(id, comment);

        return "redirect:/feed/post/" + id;
    }

    @GetMapping
    public String getFeed(Model model) {
        List<PostDto> feed = postService.getSortedFeed();
        model.addAttribute("feed", feed);

        return "feed";
    }

    @GetMapping("/tags/")
    public String getFeedWithChosenTags(@RequestParam(name = "tagsString") String tagsString, Model model) {
        List<PostDto> feedWithChosenTags = postService.getFeedWithChosenTags(tagsString);
        model.addAttribute("feed", feedWithChosenTags);

        return "feed";
    }

    @GetMapping("/post/{id}")
    public String getPostById(@PathVariable(name = "id") int id, Model model) {
        PostDto postDto = postService.getPostById(id);
        model.addAttribute("postDto", postDto);

        return "post";
    }

    @GetMapping("/pages/{postsOnPage}/{pageNumber}")
    public String getFeedSplittedByPages(@PathVariable(name = "postsOnPage") int postsOnPage,
                                         @PathVariable(name = "pageNumber") int pageNumber,
                                         Model model) {
        List<PostDto> feedSplittedByPages = postService.getFeedSplittedByPages(postsOnPage, pageNumber);
        int feedFullSize = postService.getSortedFeed().size();
        Pages pages = new Pages(postsOnPage, (feedFullSize - 1) / postsOnPage + 1);
        model.addAttribute("feed", feedSplittedByPages);
        model.addAttribute("pages", pages);

        return "feed";
    }

    @PostMapping("/post/{id}/change")
    public String changePost(@ModelAttribute Post post, @PathVariable(name = "id") int id) throws IOException {
        post.setId(id);
        postService.changePost(post);

        return "redirect:/feed/post/" + id;
    }

    @PostMapping(value = "/post/{id}", params = "_method=delete")
    public String deletePost(@PathVariable(name = "id") int id) {
        postService.deletePost(id);
        List<PostDto> feed = postService.getSortedFeed();

        return "redirect:/feed";
    }

    @PostMapping(value = "/post/{id}/removeComment/{commentId}", params = "_method=delete")
    public String deleteComment(@PathVariable(name = "id") int id,
                                @PathVariable(name = "commentId") int commentId) {
        postService.deleteComment(id, commentId);

        return "redirect:/feed/post/" + id;
    }
}