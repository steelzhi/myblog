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

    public void addPost(Post post) throws IOException {
        postRepository.addPost(PostMapper.mapToPostDao(post));
    }

    public void deletePost(Long id) {
        System.out.println("Будет удален пост с id = " + id);

        postRepository.deletePost(id);
    }

    public PostDao getPostById(Long id) {
        return postRepository.getPostById(id);
    }
}
