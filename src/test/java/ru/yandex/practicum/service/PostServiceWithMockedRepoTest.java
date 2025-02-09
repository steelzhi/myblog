package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PostServiceWithMockedRepoTest.TestConfig.class)
public class PostServiceWithMockedRepoTest {
    Post post = new Post(0, "����1", null, "�����1", "���1");
    PostDto mockPostDto1WithoutId = PostMapper.mapToPostDto(post);
    PostDto mockPostDto1WithId = new PostDto(1, "����1", null, "�����1", 0, "#���1");

    PostDto mockPostDto2 = new PostDto(2, "����2", null, "�����2", 0, "#���2");

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    public PostServiceWithMockedRepoTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(postRepository);

    }

    @Test
    void testAddPostDao() throws IOException {
        Mockito.when(postRepository.addPostDto(mockPostDto1WithoutId))
                .thenReturn(mockPostDto1WithId);

        PostDto addedPostDto = postService.addPost(post);
        assertTrue(addedPostDto != null, "���� �� ��������");
        assertTrue(addedPostDto.getId() == 1, "����������� ���������� id");

        Mockito.verify(postRepository, times(1)).addPostDto(mockPostDto1WithoutId);
    }

    @Test
    void testAddLike() {
        PostDto mockPostDto1WithLike = new PostDto(1, "����1", null, "�����1", 1, "#���1");
        Mockito.when(postRepository.addLike(mockPostDto1WithLike.getId()))
                .thenReturn(mockPostDto1WithLike);

        PostDto postDtoWithLike = postService.addLike(mockPostDto1WithLike.getId());
        assertTrue(postDtoWithLike != null, "���� �� ��������");
        assertTrue(postDtoWithLike.getNumberOfLikes() == 1, "���� ����� �� ���������");

        Mockito.verify(postRepository, times(1)).addLike(mockPostDto1WithLike.getId());
    }

    @Test
    void testAddComment() {
        PostDto mockPostDtoWithComment = new PostDto(1, "����1", null, "�����1", 1, "#���1");
        Comment comment = new Comment(0, 1, "�����������");
        mockPostDtoWithComment.getCommentsList().add(comment);
        Mockito.when(postRepository.addComment(mockPostDtoWithComment.getId(), comment.getText()))
                .thenReturn(mockPostDtoWithComment);

        PostDto postDtoWithComment = postService.addComment(1, comment);
        assertTrue(postDtoWithComment != null, "���� �� ��������");
        assertTrue(postDtoWithComment.getCommentsList().contains(comment), "����������� ����� �� ��������");

        Mockito.verify(postRepository, times(1)).addComment(1, comment.getText());
    }

    @Test
    void testGetSortedFeed() {
        PostDto mockPostDto1WithId = new PostDto(1, "����1", null, "�����1", 0, "#���1");
        PostDto mockPostDto2WithId = new PostDto(2, "����2", null, "�����2", 0, "#���2");
        Mockito.when(postRepository.getSortedFeed())
                .thenReturn(List.of(mockPostDto2WithId, mockPostDto1WithId));

        List<PostDto> sortedFeed = postService.getSortedFeed();
        assertTrue(!sortedFeed.isEmpty(), "����� �� ���������");
        assertTrue(sortedFeed.size() == 2, "���������� ������ � ����� �� ��������� � ���������� ������������ ����������� ������");
        assertEquals(sortedFeed.get(0), mockPostDto2WithId, "������������ ������������������ ������ � �����");
        assertEquals(sortedFeed.get(1), mockPostDto1WithId, "������������ ������������������ ������ � �����");

        Mockito.verify(postRepository, times(1)).getSortedFeed();
    }

    @Test
    void testGetFeedWithChosenTags() {
        String tagString1 = "#���1";
        String tagString2 = "#���2";
        PostDto mockPostDto1WithId = new PostDto(1, "����1", null, "�����1", 0, "#���1");
        PostDto mockPostDto2WithId = new PostDto(2, "����2", null, "�����2", 0, "#���2");
        Mockito.when(postRepository.getFeedWithChosenTags("('" + tagString1 + "')"))
                .thenReturn(List.of(mockPostDto1WithId));
        Mockito.when(postRepository.getFeedWithChosenTags("('" + tagString1 + "','" + tagString2 + "')"))
                .thenReturn(List.of(mockPostDto2WithId, mockPostDto1WithId));

        List<PostDto> postDtosWithTag1 = postService.getFeedWithChosenTags(tagString1);
        assertTrue(postDtosWithTag1.size() == 1, "� ����� ������ ���� 1 ����");
        assertEquals(postDtosWithTag1.get(0), mockPostDto1WithId, "� ����� ������ ���� ���� � id = 1");

        List<PostDto> postDtosWithTag2 = postService.getFeedWithChosenTags(tagString1 + ", " + tagString2);
        assertTrue(postDtosWithTag2.size() == 2, "� ����� ������ ���� 2 �����");
        assertEquals(postDtosWithTag2.get(0), mockPostDto2WithId, "������ � ����� ������ ���� ���� � id = 2");

        Mockito.verify(postRepository, times(1)).getFeedWithChosenTags("('" + tagString1 + "')");
        Mockito.verify(postRepository, times(1)).getFeedWithChosenTags("('" + tagString1 + "','" + tagString2 + "')");
    }

    @Test
    void testGetPostById() {
        PostDto mockPostDto1WithId = new PostDto(1, "����1", null, "�����1", 0, "#���1");
        Mockito.when(postRepository.getPostById(1))
                .thenReturn(mockPostDto1WithId);

        PostDto postDto = postService.getPostById(1);
        assertNotNull(postDto, "���� ������ ������������");
        assertEquals(postDto, mockPostDto1WithId);

        Mockito.verify(postRepository, times(1)).getPostById(1);
    }

    @Test
    void testGetFeedSplittedByPages() {
        PostDto mockPostDto1WithId = new PostDto(1, "����1", null, "�����1", 0, "#���1");
        PostDto mockPostDto2WithId = new PostDto(2, "����2", null, "�����2", 0, "#���2");
        PostDto mockPostDto3WithId = new PostDto(3, "����3", null, "�����3", 0, "#���3");
        Mockito.when(postRepository.getFeedSplittedByPages(2, 1))
                .thenReturn(List.of(mockPostDto3WithId, mockPostDto2WithId));
        Mockito.when(postRepository.getFeedSplittedByPages(2, 2))
                .thenReturn(List.of(mockPostDto1WithId));

        List<PostDto> postDtosSplittedByPagesPage1 = postService.getFeedSplittedByPages(2, 1);
        assertTrue(!postDtosSplittedByPagesPage1.isEmpty(), "�� �������� ������ ���� 2 �����");
        assertTrue(postDtosSplittedByPagesPage1.size() == 2, "�� �������� ������ ���� 2 �����");
        assertEquals(postDtosSplittedByPagesPage1.get(0), mockPostDto3WithId, "�� �������� ������ ���� ���� ��� ������� 3");

        List<PostDto> postDtosSplittedByPagesPage2 = postService.getFeedSplittedByPages(2, 2);
        assertTrue(!postDtosSplittedByPagesPage2.isEmpty(), "�� �������� ������ ���� 1 ����");
        assertTrue(postDtosSplittedByPagesPage2.size() == 1, "�� �������� ������ ���� 1 ����");
        assertEquals(postDtosSplittedByPagesPage2.get(0), mockPostDto1WithId, "�� �������� ������ ���� ���� ��� ������� 1");

        Mockito.verify(postRepository, times(1)).getFeedSplittedByPages(2, 1);
        Mockito.verify(postRepository, times(1)).getFeedSplittedByPages(2, 2);
    }

    @Test
    void testChangePost() throws IOException {
        Post mockChangedPost = new Post(1, "�� ����1", null, "�� �����1",  "#�����1");
        PostDto mockChangedPostDto = PostMapper.mapToPostDto(mockChangedPost);
        Mockito.when(postRepository.changePost(mockChangedPostDto))
                .thenReturn(mockChangedPostDto);

        PostDto changedPostDto = postService.changePost(mockChangedPost);
        assertTrue(changedPostDto != null, "����� ��� � �����");
        assertEquals(changedPostDto, mockChangedPostDto, "���� �� �������� ��� ������� �����������");

        Mockito.verify(postRepository, times(1)).changePost(mockChangedPostDto);
    }

    @Test
    void testDeletePost() {
        postService.deletePost(1);
        Mockito.verify(postRepository, times(1)).deletePost(1);
    }

    @Test
    void testDeleteComment() {
        PostDto mockPostDtoWithComment = new PostDto(1, "����1", null, "�����1", 0, "#���1");
        Comment comment = new Comment(1, 1, "�����������");
        mockPostDtoWithComment.getCommentsList().add(comment);
        PostDto mockPostDtoWithoutComment = new PostDto(1, "����1", null, "�����1", 0, "#���1");
        Mockito.when(postRepository.deleteComment(mockPostDtoWithoutComment.getId(), comment.getId()))
                .thenReturn(mockPostDtoWithoutComment);

        PostDto postDtoWithoutComment = postService.deleteComment(mockPostDtoWithoutComment.getId(), comment.getId());
        assertTrue(postDtoWithoutComment != null, "���� ������ ������������ ����� �������� �����������");
        assertTrue(postDtoWithoutComment.getCommentsList().isEmpty(), "������ ������������ � ����� ������ ���� ����");

        Mockito.verify(postRepository, times(1)).deleteComment(mockPostDtoWithoutComment.getId(), comment.getId());
    }

    @Configuration
    static class TestConfig {

        @Bean
        public PostService postService(PostRepository postRepository) {
            return new PostService(postRepository);
        }

        @Bean
        public PostRepository postRepository() {
            return mock(PostRepository.class);
        }
    }

    private String mapListTextsToString(List<String> tagsTextList) {
        StringBuilder sb = new StringBuilder();
        for (String tagText : tagsTextList) {
            sb.append(tagText.trim()).append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }
}
