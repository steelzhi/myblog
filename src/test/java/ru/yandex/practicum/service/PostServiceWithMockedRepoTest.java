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
    Post post = new Post(0, "Пост1", null, "Текст1", "Тег1");
    PostDto mockPostDto1WithoutId = PostMapper.mapToPostDto(post);
    PostDto mockPostDto1WithId = new PostDto(1, "Пост1", null, "Текст1", 0, "#Тег1");

    PostDto mockPostDto2 = new PostDto(2, "Пост2", null, "Текст2", 0, "#Тег2");

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
        assertTrue(addedPostDto != null, "Пост не добавлен");
        assertTrue(addedPostDto.getId() == 1, "Неправильно установлен id");

        Mockito.verify(postRepository, times(1)).addPostDto(mockPostDto1WithoutId);
    }

    @Test
    void testAddLike() {
        PostDto mockPostDto1WithLike = new PostDto(1, "Пост1", null, "Текст1", 1, "#Тег1");
        Mockito.when(postRepository.addLike(mockPostDto1WithLike.getId()))
                .thenReturn(mockPostDto1WithLike);

        PostDto postDtoWithLike = postService.addLike(mockPostDto1WithLike.getId());
        assertTrue(postDtoWithLike != null, "Пост не добавлен");
        assertTrue(postDtoWithLike.getNumberOfLikes() == 1, "Лайк посту не поставлен");

        Mockito.verify(postRepository, times(1)).addLike(mockPostDto1WithLike.getId());
    }

    @Test
    void testAddComment() {
        PostDto mockPostDtoWithComment = new PostDto(1, "Пост1", null, "Текст1", 1, "#Тег1");
        Comment comment = new Comment(0, 1, "Комментарий");
        mockPostDtoWithComment.getCommentsList().add(comment);
        Mockito.when(postRepository.addComment(mockPostDtoWithComment.getId(), comment.getText()))
                .thenReturn(mockPostDtoWithComment);

        PostDto postDtoWithComment = postService.addComment(1, comment);
        assertTrue(postDtoWithComment != null, "Пост не добавлен");
        assertTrue(postDtoWithComment.getCommentsList().contains(comment), "Комментарий посту не добавлен");

        Mockito.verify(postRepository, times(1)).addComment(1, comment.getText());
    }

    @Test
    void testGetSortedFeed() {
        PostDto mockPostDto1WithId = new PostDto(1, "Пост1", null, "Текст1", 0, "#Тег1");
        PostDto mockPostDto2WithId = new PostDto(2, "Пост2", null, "Текст2", 0, "#Тег2");
        Mockito.when(postRepository.getSortedFeed())
                .thenReturn(List.of(mockPostDto2WithId, mockPostDto1WithId));

        List<PostDto> sortedFeed = postService.getSortedFeed();
        assertTrue(!sortedFeed.isEmpty(), "Посты не добавлены");
        assertTrue(sortedFeed.size() == 2, "Количество постов в ленте не совпадает с фактически добавленными количеством постов");
        assertEquals(sortedFeed.get(0), mockPostDto2WithId, "Неправильная последовательность постов в ленте");
        assertEquals(sortedFeed.get(1), mockPostDto1WithId, "Неправильная последовательность постов в ленте");

        Mockito.verify(postRepository, times(1)).getSortedFeed();
    }

    @Test
    void testGetFeedWithChosenTags() {
        String tagString1 = "#Тег1";
        String tagString2 = "#Тег2";
        PostDto mockPostDto1WithId = new PostDto(1, "Пост1", null, "Текст1", 0, "#Тег1");
        PostDto mockPostDto2WithId = new PostDto(2, "Пост2", null, "Текст2", 0, "#Тег2");
        Mockito.when(postRepository.getFeedWithChosenTags("('" + tagString1 + "')"))
                .thenReturn(List.of(mockPostDto1WithId));
        Mockito.when(postRepository.getFeedWithChosenTags("('" + tagString1 + "','" + tagString2 + "')"))
                .thenReturn(List.of(mockPostDto2WithId, mockPostDto1WithId));

        List<PostDto> postDtosWithTag1 = postService.getFeedWithChosenTags(tagString1);
        assertTrue(postDtosWithTag1.size() == 1, "В ленте должен быть 1 пост");
        assertEquals(postDtosWithTag1.get(0), mockPostDto1WithId, "В ленте должен быть пост с id = 1");

        List<PostDto> postDtosWithTag2 = postService.getFeedWithChosenTags(tagString1 + ", " + tagString2);
        assertTrue(postDtosWithTag2.size() == 2, "В ленте должно быть 2 поста");
        assertEquals(postDtosWithTag2.get(0), mockPostDto2WithId, "Первым в ленте должен быть пост с id = 2");

        Mockito.verify(postRepository, times(1)).getFeedWithChosenTags("('" + tagString1 + "')");
        Mockito.verify(postRepository, times(1)).getFeedWithChosenTags("('" + tagString1 + "','" + tagString2 + "')");
    }

    @Test
    void testGetPostById() {
        PostDto mockPostDto1WithId = new PostDto(1, "Пост1", null, "Текст1", 0, "#Тег1");
        Mockito.when(postRepository.getPostById(1))
                .thenReturn(mockPostDto1WithId);

        PostDto postDto = postService.getPostById(1);
        assertNotNull(postDto, "Пост должен существовать");
        assertEquals(postDto, mockPostDto1WithId);

        Mockito.verify(postRepository, times(1)).getPostById(1);
    }

    @Test
    void testGetFeedSplittedByPages() {
        PostDto mockPostDto1WithId = new PostDto(1, "Пост1", null, "Текст1", 0, "#Тег1");
        PostDto mockPostDto2WithId = new PostDto(2, "Пост2", null, "Текст2", 0, "#Тег2");
        PostDto mockPostDto3WithId = new PostDto(3, "Пост3", null, "Текст3", 0, "#Тег3");
        Mockito.when(postRepository.getFeedSplittedByPages(2, 1))
                .thenReturn(List.of(mockPostDto3WithId, mockPostDto2WithId));
        Mockito.when(postRepository.getFeedSplittedByPages(2, 2))
                .thenReturn(List.of(mockPostDto1WithId));

        List<PostDto> postDtosSplittedByPagesPage1 = postService.getFeedSplittedByPages(2, 1);
        assertTrue(!postDtosSplittedByPagesPage1.isEmpty(), "На странице должно быть 2 поста");
        assertTrue(postDtosSplittedByPagesPage1.size() == 2, "На странице должно быть 2 поста");
        assertEquals(postDtosSplittedByPagesPage1.get(0), mockPostDto3WithId, "На странице должен быть пост под номером 3");

        List<PostDto> postDtosSplittedByPagesPage2 = postService.getFeedSplittedByPages(2, 2);
        assertTrue(!postDtosSplittedByPagesPage2.isEmpty(), "На странице должен быть 1 пост");
        assertTrue(postDtosSplittedByPagesPage2.size() == 1, "На странице должен быть 1 пост");
        assertEquals(postDtosSplittedByPagesPage2.get(0), mockPostDto1WithId, "На странице должен быть пост под номером 1");

        Mockito.verify(postRepository, times(1)).getFeedSplittedByPages(2, 1);
        Mockito.verify(postRepository, times(1)).getFeedSplittedByPages(2, 2);
    }

    @Test
    void testChangePost() throws IOException {
        Post mockChangedPost = new Post(1, "Не пост1", null, "Не текст1",  "#НеТег1");
        PostDto mockChangedPostDto = PostMapper.mapToPostDto(mockChangedPost);
        Mockito.when(postRepository.changePost(mockChangedPostDto))
                .thenReturn(mockChangedPostDto);

        PostDto changedPostDto = postService.changePost(mockChangedPost);
        assertTrue(changedPostDto != null, "Поста нет в ленте");
        assertEquals(changedPostDto, mockChangedPostDto, "Пост не изменени или изменен неправильно");

        Mockito.verify(postRepository, times(1)).changePost(mockChangedPostDto);
    }

    @Test
    void testDeletePost() {
        postService.deletePost(1);
        Mockito.verify(postRepository, times(1)).deletePost(1);
    }

    @Test
    void testDeleteComment() {
        PostDto mockPostDtoWithComment = new PostDto(1, "Пост1", null, "Текст1", 0, "#Тег1");
        Comment comment = new Comment(1, 1, "Комментарий");
        mockPostDtoWithComment.getCommentsList().add(comment);
        PostDto mockPostDtoWithoutComment = new PostDto(1, "Пост1", null, "Текст1", 0, "#Тег1");
        Mockito.when(postRepository.deleteComment(mockPostDtoWithoutComment.getId(), comment.getId()))
                .thenReturn(mockPostDtoWithoutComment);

        PostDto postDtoWithoutComment = postService.deleteComment(mockPostDtoWithoutComment.getId(), comment.getId());
        assertTrue(postDtoWithoutComment != null, "Пост должен существовать после удаления комментария");
        assertTrue(postDtoWithoutComment.getCommentsList().isEmpty(), "Список комментариев у поста должен быть пуст");

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
