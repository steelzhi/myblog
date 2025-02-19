package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostDto addPost(Post post) throws IOException {
        return postRepository.addPostDto(PostMapper.mapToPostDto(post));
    }

    public PostDto addLike(int id) {
        return postRepository.addLike(id);
    }

    public PostDto addComment(int postId, Comment comment) {
        return postRepository.addComment(postId, comment.getText());
    }

    public List<PostDto> getSortedFeed() {
        return postRepository.getSortedFeed();
    }

    public List<PostDto> getFeedWithChosenTags(String tagsString) {
        String[] tagsArray = tagsString.split(",");
        String tagsInString = mapListTextsToString(List.of(tagsArray));
        return postRepository.getFeedWithChosenTags(tagsInString);
    }

    public PostDto getPostById(int id) {
        return postRepository.getPostById(id);
    }

    public List<PostDto> getFeedSplittedByPages(int postsOnPage, int pageNumber) {
        return postRepository.getFeedSplittedByPages(postsOnPage, pageNumber);
    }

    public PostDto changePost(Post changedPost) throws IOException {
        return postRepository.changePost(PostMapper.mapToPostDto(changedPost));
    }

    public PostDto changeComment(int id, int postId, String text) {
        return postRepository.changeComment(id, postId, text);
    }

    public void deletePost(int id) {
        postRepository.deletePost(id);
    }

    public PostDto deleteComment(int postDtoId, int commentId) {
        return postRepository.deleteComment(postDtoId, commentId);
    }

    public byte[] getImage(int postDtoId) {
        return postRepository.getImage(postDtoId);
    }

    private String mapListTextsToString(List<String> tagsTextList) {
        StringBuilder sb = new StringBuilder("('");
        for (String tagText : tagsTextList) {
            if (!tagText.trim().startsWith("#")) {
                sb.append("#");
            }
            sb.append(tagText.trim()).append("','");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");
        return sb.toString();
    }
}
