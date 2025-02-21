package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostResponseDto addPost(Post post) throws IOException {
        return postRepository.addPostDto(PostMapper.mapToPostRequestDto(post));
    }

    public List<PostResponseDto> getSortedFeed() {
        return postRepository.getSortedFeed();
    }

    public List<PostResponseDto> getFeedWithChosenTags(String tagsString) throws SQLException {
        String[] tagsArray = tagsString.split(",");
        String tagsInString = mapListTextsToString(List.of(tagsArray));
        return postRepository.getFeedWithChosenTags(tagsInString);
    }

    public PostResponseDto getPostById(int id) {
        return postRepository.getPostById(id);
    }

    public List<PostResponseDto> getFeedSplittedByPages(int postsOnPage, int pageNumber) {
        return postRepository.getFeedSplittedByPages(postsOnPage, pageNumber);
    }

    public PostResponseDto changePost(Post changedPost) throws IOException {
        return postRepository.changePost(PostMapper.mapToPostRequestDto(changedPost));
    }

    public void deletePost(int id) {
        postRepository.deletePost(id);
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
