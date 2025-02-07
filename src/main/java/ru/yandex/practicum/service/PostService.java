package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.io.*;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<PostDao> getSortedFeed() {
        return postRepository.getSortedFeed();
    }

    public List<PostDao> getFeedWithChosenTags(String tagsString) {
        return postRepository.getFeedWithChosenTags(tagsString);
    }

    public void addPost(Post post) throws IOException {
        postRepository.addPostDao(PostMapper.mapToPostDao(post));
    }

    public void changePost(Post changedPost) throws IOException {
        postRepository.changePost(PostMapper.mapToPostDao(changedPost));
    }

    public void addLike(Integer id) {
        postRepository.addLike(id);
    }

    public void deletePost(Long id) {
        System.out.println("Будет удален пост с id = " + id);

        postRepository.deletePost(id);
    }

    public PostDao getPostById(Long id) {
        return postRepository.getPostById(id);
    }
}
