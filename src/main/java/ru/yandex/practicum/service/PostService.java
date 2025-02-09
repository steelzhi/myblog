package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.io.*;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public void addPost(Post post) throws IOException {
        postRepository.addPostDto(PostMapper.mapToPostDto(post));
    }

    public void addLike(Integer id) {
        postRepository.addLike(id);
    }

    public void addComment(int postId, Comment comment) {
        postRepository.addComment(postId, comment.getText());
    }

    public List<PostDto> getSortedFeed() {
        return postRepository.getSortedFeed();
    }

    public List<PostDto> getFeedWithChosenTags(String tagsString) {
        String[] tagsArray = tagsString.split(",");
        String tagsInString = mapListTextsToString(List.of(tagsArray));
        return postRepository.getFeedWithChosenTags(tagsInString);
    }

    public PostDto getPostById(Long id) {
        return postRepository.getPostById(id);
    }

    public List<PostDto> getFeedSplittedByPages(int postsOnPage, int pageNumber) {
        return postRepository.getFeedSplittedByPages(postsOnPage, pageNumber);
    }

    public void changePost(Post changedPost) throws IOException {
        postRepository.changePost(PostMapper.mapToPostDto(changedPost));
    }

    public void deletePost(Long id) {
        postRepository.deletePost(id);
    }

    public void deleteComment(Long postDtoId, Long commentId) {
        postRepository.deleteComment(postDtoId, commentId);
    }

    private String mapListTextsToString(List<String> tagsTextList) {
        StringBuilder sb = new StringBuilder("('");
        for (String tagText : tagsTextList) {
            sb.append(tagText.trim()).append("','");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");
        return sb.toString();
    }
}
