package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dao.PostDao;
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
        postRepository.addPostDao(PostMapper.mapToPostDao(post));
    }

    public void addLike(Integer id) {
        postRepository.addLike(id);
    }

    public void addComment(int postId, Comment comment) {
        postRepository.addComment(postId, comment.getText());
    }

    public List<PostDao> getSortedFeed() {
        return postRepository.getSortedFeed();
    }

    public List<PostDao> getFeedWithChosenTags(String tagsString) {
        String[] tagsArray = tagsString.split(",");
        String tagsInString = mapListTextsToString(List.of(tagsArray));
        return postRepository.getFeedWithChosenTags(tagsInString);
    }

    public PostDao getPostById(Long id) {
        return postRepository.getPostById(id);
    }

    public List<PostDao> getFeedSplittedByPages(int postsOnPage, int pageNumber) {
        return postRepository.getFeedSplittedByPages(postsOnPage, pageNumber);
    }

    public void changePost(Post changedPost) throws IOException {
        postRepository.changePost(PostMapper.mapToPostDao(changedPost));
    }

    public void deletePost(Long id) {
        postRepository.deletePost(id);
    }

    public void deleteComment(Long postDaoId, Long commentId) {
        postRepository.deleteComment(postDaoId, commentId);
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
