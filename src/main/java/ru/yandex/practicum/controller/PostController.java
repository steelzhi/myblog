package ru.yandex.practicum.controller;

import org.springframework.data.relational.core.sql.In;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Pages;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/feed")
public class PostController {
    public static final int POSTS_ON_PAGE_DEFAULT = 10;
    public static final int PAGE_NUMBER_FIRST = 1;

    @Autowired
    private PostService postService;

    @PostMapping
    public String addPost(@ModelAttribute Post post) throws IOException {
        postService.addPost(post);
        return "redirect:/feed";
    }

    @GetMapping("/tags/")
    public String getFeedWithChosenTags(@RequestParam(name = "tagsString") String tagsString, Model model) throws SQLException {
        List<PostResponseDto> feedWithChosenTags = postService.getFeedWithChosenTags(tagsString);
        model.addAttribute("feed", feedWithChosenTags);

        return "feed";
    }

    @GetMapping("/post/{id}")
    public String getPostById(@PathVariable(name = "id") int id, Model model) {
        PostResponseDto postResponseDto = postService.getPostById(id);
        model.addAttribute("postResponseDto", postResponseDto);

        return "post";
    }

    @GetMapping
    public String getFeedSplittedByPages(@RequestParam(name = "postsOnPage", required = false) Integer postsOnPage,
                                         @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                         Model model) {
        if (postsOnPage == null) {
            postsOnPage = POSTS_ON_PAGE_DEFAULT;
            pageNumber = PAGE_NUMBER_FIRST;
        }
        List<PostResponseDto> feedSplittedByPages = postService.getFeedSplittedByPages(postsOnPage, pageNumber);
        int feedFullSize = postService.getFeedSize();
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

        return "redirect:/feed";
    }
}