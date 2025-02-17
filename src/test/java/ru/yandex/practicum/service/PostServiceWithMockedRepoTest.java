package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = PostService.class)
public class PostServiceWithMockedRepoTest {
    Post post = new Post(0, "Post1", null, "Text1", "Tag1");
    PostDto mockPostDto1WithoutId = PostMapper.mapToPostDto(post);
    PostDto mockPostDto1WithId
            = new PostDto(1, "Post1", null, "Text1", 0, "#Tag1");

    PostDto mockPostDto2
            = new PostDto(2, "Post2", null, "Text2", 0, "#Tag2");

    @Autowired
    PostService postService;

    @MockitoBean
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
        assertTrue(addedPostDto != null, "Post wasn't added");
        assertTrue(addedPostDto.getId() == 1, "Id is incorrect");

        Mockito.verify(postRepository, times(1)).addPostDto(mockPostDto1WithoutId);
    }

    @Test
    void testAddLike() {
        PostDto mockPostDto1WithLike
                = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        Mockito.when(postRepository.addLike(mockPostDto1WithLike.getId()))
                .thenReturn(mockPostDto1WithLike);

        PostDto postDtoWithLike = postService.addLike(mockPostDto1WithLike.getId());
        assertTrue(postDtoWithLike != null, "Post wasn't added");
        assertTrue(postDtoWithLike.getNumberOfLikes() == 1, "Like wasn't added to post");

        Mockito.verify(postRepository, times(1)).addLike(mockPostDto1WithLike.getId());
    }

    @Test
    void testAddComment() {
        PostDto mockPostDtoWithComment
                = new PostDto(1, "Post1", null, "Text1", 1, "#Tag1");
        Comment comment = new Comment(0, 1, "Comment");
        mockPostDtoWithComment.getCommentsList().add(comment);
        Mockito.when(postRepository.addComment(mockPostDtoWithComment.getId(), comment.getText()))
                .thenReturn(mockPostDtoWithComment);

        PostDto postDtoWithComment = postService.addComment(1, comment);
        assertTrue(postDtoWithComment != null, "Post wasn't added");
        assertTrue(postDtoWithComment.getCommentsList().contains(comment), "Comment comment);\n" +
                "        assertTrue(postDtoWithComment != null, \"Post wasn't added");

        Mockito.verify(postRepository, times(1)).addComment(1, comment.getText());
    }

    @Test
    void testGetSortedFeed() {
        PostDto mockPostDto1WithId
                = new PostDto(1, "Post1", null, "Text1", 0, "#Tag1");
        PostDto mockPostDto2WithId
                = new PostDto(2, "Post2", null, "Text2", 0, "#Tag2");
        Mockito.when(postRepository.getSortedFeed())
                .thenReturn(List.of(mockPostDto2WithId, mockPostDto1WithId));

        List<PostDto> sortedFeed = postService.getSortedFeed();
        assertTrue(!sortedFeed.isEmpty(), "Posts weren't added");
        assertTrue(sortedFeed.size() == 2, "Number of posts is incorrect");
        assertEquals(sortedFeed.get(0), mockPostDto2WithId, "Wrong sequence of posts in feed");
        assertEquals(sortedFeed.get(1), mockPostDto1WithId, "Wrong sequence of posts in feed");

        Mockito.verify(postRepository, times(1)).getSortedFeed();
    }

    @Test
    void testGetFeedWithChosenTags() {
        String tagString1 = "#Tag1";
        String tagString2 = "#Tag2";
        PostDto mockPostDto1WithId
                = new PostDto(1, "Post1", null, "Text1", 0, "#Tag1");
        PostDto mockPostDto2WithId
                = new PostDto(2, "Post2", null, "Text2", 0, "#Tag2");
        Mockito.when(postRepository.getFeedWithChosenTags("('" + tagString1 + "')"))
                .thenReturn(List.of(mockPostDto1WithId));
        Mockito.when(postRepository.getFeedWithChosenTags("('" + tagString1 + "','" + tagString2 + "')"))
                .thenReturn(List.of(mockPostDto2WithId, mockPostDto1WithId));

        List<PostDto> postDtosWithTag1 = postService.getFeedWithChosenTags(tagString1);
        assertTrue(postDtosWithTag1.size() == 1, "Feed should contain 1 post");
        assertEquals(postDtosWithTag1.get(0), mockPostDto1WithId, "Feed should contain post with id = 1");

        List<PostDto> postDtosWithTag2 = postService.getFeedWithChosenTags(tagString1 + ", " + tagString2);
        assertTrue(postDtosWithTag2.size() == 2, "Feed should contain 2 posts");
        assertEquals(postDtosWithTag2.get(0), mockPostDto2WithId, "First post in the feed should have id = 2");

        Mockito.verify(postRepository, times(1))
                .getFeedWithChosenTags("('" + tagString1 + "')");
        Mockito.verify(postRepository, times(1))
                .getFeedWithChosenTags("('" + tagString1 + "','" + tagString2 + "')");
    }

    @Test
    void testGetPostById() {
        PostDto mockPostDto1WithId
                = new PostDto(1, "Post1", null, "Text1", 0, "#Tag1");
        Mockito.when(postRepository.getPostById(1))
                .thenReturn(mockPostDto1WithId);

        PostDto postDto = postService.getPostById(1);
        assertNotNull(postDto, "Post should exist");
        assertEquals(postDto, mockPostDto1WithId);

        Mockito.verify(postRepository, times(1)).getPostById(1);
    }

    @Test
    void testGetFeedSplittedByPages() {
        PostDto mockPostDto1WithId
                = new PostDto(1, "Post1", null, "Text1", 0, "#Tag1");
        PostDto mockPostDto2WithId
                = new PostDto(2, "Post2", null, "Text2", 0, "#Tag2");
        PostDto mockPostDto3WithId
                = new PostDto(3, "Post3", null, "Text3", 0, "#Tag3");
        Mockito.when(postRepository.getFeedSplittedByPages(2, 1))
                .thenReturn(List.of(mockPostDto3WithId, mockPostDto2WithId));
        Mockito.when(postRepository.getFeedSplittedByPages(2, 2))
                .thenReturn(List.of(mockPostDto1WithId));

        List<PostDto> postDtosSplittedByPagesPage1 = postService.getFeedSplittedByPages(2, 1);
        assertTrue(!postDtosSplittedByPagesPage1.isEmpty(), "Page should have 2 posts");
        assertTrue(postDtosSplittedByPagesPage1.size() == 2, "Page should have 2 posts");
        assertEquals(postDtosSplittedByPagesPage1.get(0), mockPostDto3WithId, "Page should have post # 3");

        List<PostDto> postDtosSplittedByPagesPage2 = postService.getFeedSplittedByPages(2, 2);
        assertTrue(!postDtosSplittedByPagesPage2.isEmpty(), "Page should have 1 post");
        assertTrue(postDtosSplittedByPagesPage2.size() == 1, "Page should have 1 post");
        assertEquals(postDtosSplittedByPagesPage2.get(0), mockPostDto1WithId, "Page should have post # 1");

        Mockito.verify(postRepository, times(1))
                .getFeedSplittedByPages(2, 1);
        Mockito.verify(postRepository, times(1))
                .getFeedSplittedByPages(2, 2);
    }

    @Test
    void testChangePost() throws IOException {
        Post mockChangedPost = new Post(1, "Not post1", null, "Not text1",  "#NotTag1");
        PostDto mockChangedPostDto = PostMapper.mapToPostDto(mockChangedPost);
        Mockito.when(postRepository.changePost(mockChangedPostDto))
                .thenReturn(mockChangedPostDto);

        PostDto changedPostDto = postService.changePost(mockChangedPost);
        assertTrue(changedPostDto != null, "Feed doesn't contain post");
        assertEquals(changedPostDto, mockChangedPostDto, "Post wasn't changed or was changed incorrectly");

        Mockito.verify(postRepository, times(1)).changePost(mockChangedPostDto);
    }

    @Test

    void changeComment() {
        PostDto mockPostDto
                = new PostDto(10, "Post 10", null, "Text 10", 0, "Tag10");
        Comment comment = new Comment(10, mockPostDto.getId(), "Comment 10");
        mockPostDto.getCommentsList().add(comment);
        String changedText = "Changed text 10";
        mockPostDto.getCommentsList().get(0).setText(changedText);

        Mockito.when(postRepository.changeComment(mockPostDto.getId(), comment.getId(), changedText))
                .thenReturn(mockPostDto);
        PostDto postDtoWithChangedComment
                = postService.changeComment(mockPostDto.getId(), comment.getId(), changedText);
        assertTrue(postDtoWithChangedComment != null, "Feed doesn't contain post");
        assertEquals(postDtoWithChangedComment, mockPostDto, "Post wasn't changed or was changed incorrectly");

        Mockito.verify(postRepository, times(1))
                .changeComment(mockPostDto.getId(), comment.getId(), changedText);
    }


    @Test
    void testDeletePost() {
        postService.deletePost(1);
        Mockito.verify(postRepository, times(1)).deletePost(1);
    }

    @Test
    void testDeleteComment() {
        PostDto mockPostDtoWithComment
                = new PostDto(1, "Post1", null, "Text1", 0, "#Tag1");
        Comment comment = new Comment(1, 1, "Comment");
        mockPostDtoWithComment.getCommentsList().add(comment);
        PostDto mockPostDtoWithoutComment
                = new PostDto(1, "Post1", null, "Text1", 0, "#Tag1");
        Mockito.when(postRepository.deleteComment(mockPostDtoWithoutComment.getId(), comment.getId()))
                .thenReturn(mockPostDtoWithoutComment);

        PostDto postDtoWithoutComment = postService.deleteComment(mockPostDtoWithoutComment.getId(), comment.getId());
        assertTrue(postDtoWithoutComment != null, "Post should exist after comment deleting");
        assertTrue(postDtoWithoutComment.getCommentsList().isEmpty(), "Comments list should be empty");

        Mockito.verify(postRepository, times(1))
                .deleteComment(mockPostDtoWithoutComment.getId(), comment.getId());
    }
}
