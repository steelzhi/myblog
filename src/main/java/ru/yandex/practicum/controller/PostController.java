package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ru.yandex.practicum.dao.PostDao;
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
    public String addLike(@PathVariable(name = "id") Integer id) throws IOException {
        postService.addLike(id);

        return "redirect:/feed/post/" + id;
    }

    @PostMapping("/post/addComment/{id}")
    public String addComment(@PathVariable(name = "id") int id, @ModelAttribute Comment comment) {
        postService.addComment(id, comment);

        return "redirect:/feed/post/" + id;
    }

    @GetMapping
    public String getFeed(Model model) {
        List<PostDao> feed = postService.getSortedFeed();
        model.addAttribute("feed", feed);
        Pages pages = new Pages(feed.size(), 1);
        model.addAttribute("pages", pages);

        return "feed";
    }

    @GetMapping("/tags/")
    public String getFeedWithChosenTags(@RequestParam(name = "tagsString") String tagsString, Model model) {
        List<PostDao> feedWithChosenTags = postService.getFeedWithChosenTags(tagsString);
        model.addAttribute("feed", feedWithChosenTags);

        return "feed";
    }

    @GetMapping("/post/{id}")
    public String getPostById(@PathVariable(name = "id") Long id, Model model) {
        PostDao postDao = postService.getPostById(id);
        model.addAttribute("postDao", postDao);

        return "post";
    }

    @GetMapping("/pages")
    public String getFeedSplittedByPages(@RequestParam(name = "postsOnPage") int postsOnPage,
                                         @RequestParam(name = "pageNumber") int pageNumber,
                                         Model model) {
        List<PostDao> feedSplittedByPages = postService.getFeedSplittedByPages(postsOnPage, pageNumber);
        int feedFullSize = postService.getSortedFeed().size();
        Pages pages = new Pages(postsOnPage, feedFullSize / postsOnPage + 1);
        model.addAttribute("feed", feedSplittedByPages);
        model.addAttribute("pages", pages);

        return "feed";
    }

    @PostMapping("/post/{id}/change")
    public String changePost(@ModelAttribute Post post, @PathVariable(name = "id") Integer id) throws IOException {
        post.setId(id);
        postService.changePost(post);

        return "redirect:/feed/post/" + id;
    }

    @PostMapping(value = "/post/{id}", params = "_method=delete")
    public String deletePost(@PathVariable(name = "id") Long id) {
        postService.deletePost(id);
        List<PostDao> feed = postService.getSortedFeed();

        return "redirect:/feed";
    }

    @PostMapping(value = "/post/{id}/removeComment/{commentId}", params = "_method=delete")
    public String deleteComment(@PathVariable(name = "id") Long id,
                                @PathVariable(name = "commentId") Long commentId) {
        postService.deleteComment(id, commentId);

        return "redirect:/feed/post/" + id;
    }
}