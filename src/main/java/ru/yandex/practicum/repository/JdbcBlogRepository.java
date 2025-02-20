package ru.yandex.practicum.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.*;
import org.thymeleaf.util.StringUtils;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.model.PostTag;
import ru.yandex.practicum.model.Tag;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class JdbcBlogRepository implements PostRepository {
    RowMapper<PostResponseDto> MAP_TO_POSTRESPONSEDTO = (ResultSet resultSet, int rowNum) -> new PostResponseDto(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getBytes("image"),
            resultSet.getString("text"),
            resultSet.getInt("number_of_likes"),
            null);

    RowMapper<Comment> MAP_TO_COMMENTS = (ResultSet resultSet, int rowNum) -> new Comment(
            resultSet.getInt("id"),
            resultSet.getInt("post_id"),
            resultSet.getString("text"));

    RowMapper<Tag> MAP_TO_TAG = (ResultSet resultSet, int rowNum) -> new Tag(
            resultSet.getInt("id"),
            resultSet.getString("text"));

    RowMapper<Integer> MAP_TO_ID = (ResultSet resultSet, int rowNum) -> new Integer(
            resultSet.getInt("id"));

    RowMapper<Integer> MAP_TO_TAG_ID = (ResultSet resultSet, int rowNum) -> new Integer(
            resultSet.getInt("tag_id"));

    RowMapper<Integer> MAP_TO_POST_ID = (ResultSet resultSet, int rowNum) -> new Integer(
            resultSet.getInt("post_id"));

    RowMapper<String> MAP_TO_TEXT = (ResultSet resultSet, int rowNum) -> new String(
            resultSet.getString("text"));

    RowMapper<byte[]> MAP_TO_IMAGE = (ResultSet resultSet, int rowNum) -> resultSet.getBytes("image");

    RowMapper<PostTag> MAP_TO_POST_TAG = (ResultSet resultSet, int rowNum) -> new PostTag(
            resultSet.getInt("id"),
            resultSet.getInt("post_id"),
            resultSet.getInt("tag_id"));

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public PostResponseDto addPostDto(PostRequestDto postRequestDto) {
        jdbcTemplate.update("""
                        INSERT INTO posts (name, image, text) 
                        VALUES (?, ?, ?)
                        """,
                postRequestDto.getName(), postRequestDto.getImage(), postRequestDto.getText());

        int postId = jdbcTemplate.queryForObject("""
                SELECT id
                FROM posts
                ORDER BY id DESC
                LIMIT(1);
                """, Integer.class);

        PostResponseDto postResponseDto = getPostById(postId);
        postResponseDto.getTagsTextList().addAll(postRequestDto.getTagsTextList());
        postRequestDto.setId(postId);
        addNewTags(postResponseDto);
        return postResponseDto;
    }

    @Override
    public PostResponseDto addLike(int postId) {
        jdbcTemplate.update("""
                        UPDATE posts 
                        SET number_of_likes = number_of_likes + 1
                        WHERE id = ?
                        """,
                postId
        );

        return getPostById(postId);
    }

    @Override
    public PostResponseDto addComment(int postId, String commentText) {
        jdbcTemplate.update("""
                INSERT INTO comments (post_id, text) 
                VALUES (?, ?)
                """, postId, commentText);

        return getPostById(postId);
    }

    @Override
    public List<PostResponseDto> getSortedFeed() {
        List<PostResponseDto> postDtosList = jdbcTemplate.query("""
                SELECT id, name, image, text, number_of_likes 
                FROM posts p
                """, MAP_TO_POSTRESPONSEDTO);
        return getPostResponseDtoListWithCommentsAndTags(postDtosList);
    }

    @Override
    public List<PostResponseDto> getFeedWithChosenTags(String tagsInString) {
        PreparedStatementCreator psc = con -> {
            String[] tagArray = tagsInString.substring(2, tagsInString.length() - 2).split("','");
            StringBuilder sb = getStringWithPlaceHolders(tagArray.length);
            PreparedStatement selectPosts = con.prepareStatement(
                    "SELECT post_id FROM posts_tags pt LEFT JOIN tags t ON t.id = pt.tag_id WHERE t.text IN ("
                            + sb + ") ORDER BY post_id DESC ");
            for (int i = 0; i < tagArray.length; i++) {
                selectPosts.setString(i + 1, tagArray[i]);
            }

            return selectPosts;
        };

        List<Integer> postDtosIdsWithTags = jdbcTemplate.query(psc, MAP_TO_POST_ID);

        if (postDtosIdsWithTags.isEmpty()) {
            return new ArrayList<>();
        }

        psc = con -> {
            StringBuilder sb = getStringWithPlaceHolders(postDtosIdsWithTags.size());
            PreparedStatement selectPosts = con.prepareStatement(
                    "SELECT id, name, image, text, number_of_likes FROM posts WHERE id IN (" + sb + ")");
            for (int i = 0; i < postDtosIdsWithTags.size(); i++) {
                selectPosts.setInt(i + 1, postDtosIdsWithTags.get(i));
            }

            return selectPosts;
        };

        List<PostResponseDto> selectedPostDtosList = jdbcTemplate.query(psc, MAP_TO_POSTRESPONSEDTO);
        return getPostResponseDtoListWithCommentsAndTags(selectedPostDtosList);
    }

    @Override
    public PostResponseDto getPostById(int id) {
        PreparedStatementCreator psc = con -> {
            PreparedStatement selectPost = con.prepareStatement("""
                    SELECT id, name, image, text, number_of_likes 
                    FROM posts 
                    WHERE id = ?
                    """);
            selectPost.setInt(1, id);

            return selectPost;
        };

        PostResponseDto postResponseDto = jdbcTemplate.query(psc, MAP_TO_POSTRESPONSEDTO).getFirst();
        List<Comment> commentList = getAllCommentsForPost(postResponseDto.getId());
        postResponseDto.getCommentsList().addAll(commentList);
        List<String> tagsTextList = getAllTagsTextForPost(postResponseDto.getId());
        postResponseDto.getTagsTextList().addAll(tagsTextList);
        return postResponseDto;
    }

    @Override
    public List<PostResponseDto> getFeedSplittedByPages(int postsOnPage, int pageNumber) {
        PreparedStatementCreator psc = con -> {
            PreparedStatement selectPost = con.prepareStatement("""
                    SELECT id, name, image, text, number_of_likes 
                    FROM posts p 
                    ORDER BY id DESC 
                    LIMIT ? 
                    OFFSET ?
                    """);
            selectPost.setInt(1, postsOnPage);
            selectPost.setInt(2, postsOnPage * (pageNumber - 1));

            return selectPost;
        };

        List<PostResponseDto> postResponseDtos = jdbcTemplate.query(psc, MAP_TO_POSTRESPONSEDTO);
        return getPostResponseDtoListWithCommentsAndTags(postResponseDtos);
    }

    @Override
    public PostResponseDto changePost(PostRequestDto changedPost) {
        jdbcTemplate.update("""
                DELETE FROM posts_tags
                WHERE post_id = ?
                """, changedPost.getId());

        jdbcTemplate.update(
                """
                        UPDATE posts 
                        SET name = ?, image = ?, text = ?
                        WHERE id = ?
                        """,
                changedPost.getName(),
                changedPost.getImage(),
                changedPost.getText(),
                changedPost.getId());

        PostResponseDto postResponseDto = getPostById(changedPost.getId());
        postResponseDto.getTagsTextList().addAll(changedPost.getTagsTextList());
        addNewTags(postResponseDto);
        return postResponseDto;
    }

    @Override
    public PostResponseDto changeComment(int id, int postId, String text) {
        jdbcTemplate.update("""
                UPDATE comments 
                SET text = ? 
                WHERE id = ? 
                    AND post_id = ?
                """, text, id, postId);
        return getPostById(postId);
    }

    @Override
    public void deletePost(int id) {
        jdbcTemplate.update("""
                DELETE FROM posts 
                WHERE id = ?
                """, id);
    }

    private List<String> getAllTagsTextForPost(int postId) {
        List<String> tagsTextList = new ArrayList<>();
        List<PostTag> postsTagsList = getPostsTagsList();
        if (postsTagsList != null && !postsTagsList.isEmpty()) {
            List<Integer> tagsIdsForPostDto = getTagsIdsForPostDto(postId);
            if (tagsIdsForPostDto != null && !tagsIdsForPostDto.isEmpty()) {

                PreparedStatementCreator psc = con -> {
                    StringBuilder sb = getStringWithPlaceHolders(tagsIdsForPostDto.size());
                    PreparedStatement selectTagsText = con.prepareStatement(
                            "SELECT text FROM tags WHERE id IN (" + sb + ")");

                    for (int i = 0; i < tagsIdsForPostDto.size(); i++) {
                        selectTagsText.setInt(i + 1, tagsIdsForPostDto.get(i));
                    }

                    return selectTagsText;
                };

                tagsTextList = jdbcTemplate.query(psc, MAP_TO_TEXT);
            }
        }

        return tagsTextList;
    }

    @Override
    public PostResponseDto deleteComment(int postDtoId, int commentId) {
        jdbcTemplate.update("""
                DELETE FROM comments
                WHERE id = ?
                """, commentId);
        return getPostById(postDtoId);
    }

    @Override
    public byte[] getImage(int postDtoId) {
        PreparedStatementCreator psc = con -> {
            PreparedStatement selectPost = con.prepareStatement("""
                    SELECT image 
                    FROM posts 
                    WHERE id = ?
                    """);
            selectPost.setInt(1, postDtoId);

            return selectPost;
        };

        byte[] image = jdbcTemplate.query(psc, MAP_TO_IMAGE).getFirst();
        return image;
    }

    // Метод для тестирования (зачищаем БД из объектной модели)
    @Override
    public void cleanAllDataBase() {
        jdbcTemplate.execute("DELETE FROM posts_tags");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM tags");
    }

    private Map<Integer, String> getTagsMap() {
        List<Tag> tagList = jdbcTemplate.query(
                """
                        SELECT id, text
                        FROM tags
                        """, MAP_TO_TAG);
        Map<Integer, String> tagMap = tagList.stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getText));

        return tagMap;
    }

    private List<PostTag> getPostsTagsList() {
        return jdbcTemplate.query("""
                SELECT id, post_id, tag_id
                FROM posts_tags
                """, MAP_TO_POST_TAG);
    }

    private List<Integer> getTagsIdsForPostDto(int postDtoId) {
        PreparedStatementCreator psc = con -> {
            PreparedStatement selectPost = con.prepareStatement(
                    """
                            SELECT tag_id
                            FROM posts_tags
                            WHERE post_id = ?
                            """);
            selectPost.setInt(1, postDtoId);

            return selectPost;
        };

        return jdbcTemplate.query(psc, MAP_TO_TAG_ID);
    }

    private void addNewTags(PostResponseDto postResponseDto) {
        Map<Integer, String> tagMap = getTagsMap();
        for (String tagText : postResponseDto.getTagsTextList()) {
            int tagId;
            if (!tagMap.containsValue(tagText)) {
                jdbcTemplate.update("""
                                INSERT INTO tags (text)
                                VALUES (?);
                                """,
                        tagText);

                tagId = jdbcTemplate.query("""
                        SELECT id
                        FROM tags
                        ORDER BY id DESC
                        LIMIT(1);
                        """, MAP_TO_ID).getFirst();
            } else {
                PreparedStatementCreator psc = con -> {
                    PreparedStatement selectPost = con.prepareStatement("""
                            SELECT id 
                            FROM tags 
                            WHERE text = ?
                            """);
                    selectPost.setString(1, tagText);

                    return selectPost;
                };

                tagId = jdbcTemplate.query(psc, MAP_TO_ID).getFirst();
            }

            jdbcTemplate.update("""
                            INSERT INTO posts_tags (post_id, tag_id)
                            VALUES (?, ?);
                            """,
                    postResponseDto.getId(), tagId);
        }
    }

    private Map<Integer, List<Comment>> getCommentsMap() {
        Map<Integer, List<Comment>> comments = new HashMap<>();
        List<Comment> commentList = jdbcTemplate.query(
                """
                        SELECT id, text, post_id
                        FROM comments
                        """, MAP_TO_COMMENTS);
        for (Comment c : commentList) {
            if (!comments.containsKey(c.getPostId())) {
                comments.put(c.getPostId(), new ArrayList<>());
            }
            comments.get(c.getPostId()).add(c);
        }

        return comments;
    }

    private List<Comment> getAllCommentsForPost(int postId) {
        PreparedStatementCreator psc = con -> {
            PreparedStatement selectPost = con.prepareStatement(
                    """
                            SELECT id, text, post_id
                            FROM comments
                            WHERE post_id = ?
                            """);
            selectPost.setInt(1, postId);

            return selectPost;
        };

        List<Comment> commentList = jdbcTemplate.query(psc, MAP_TO_COMMENTS);

        return commentList;
    }

    private void addTagsToPostDtos(Map<Integer, PostResponseDto> postDtosMap) {
        Map<Integer, String> tagsMap = getTagsMap();
        List<PostTag> postsTagsList = getPostsTagsList();
        for (PostTag pt : postsTagsList) {
            if (postDtosMap.containsKey(pt.getPostId())) {
                postDtosMap.get(pt.getPostId()).getTagsTextList().add(tagsMap.get(pt.getTagId()));
            }
        }
    }

    private void addCommentsToPostDtos(Map<Integer, PostResponseDto> postResponseDtoMap) {
        Map<Integer, List<Comment>> commentsMap = getCommentsMap();
        for (Integer postDtoId : commentsMap.keySet()) {
            if (postResponseDtoMap.containsKey(postDtoId)) {
                postResponseDtoMap.get(postDtoId).getCommentsList().addAll(commentsMap.get(postDtoId));
            }
        }
    }

    private List<PostResponseDto> getPostResponseDtoListWithCommentsAndTags(List<PostResponseDto> postResponseDtos) {
        Map<Integer, PostResponseDto> postDtosMap = postResponseDtos.stream()
                .collect(Collectors.toMap(PostResponseDto::getId, postResponseDto -> postResponseDto));

        addCommentsToPostDtos(postDtosMap);
        addTagsToPostDtos(postDtosMap);
        List<PostResponseDto> postDtoList = new ArrayList<>(postDtosMap.values());
        postDtoList.sort(getComparatorForId());
        return postDtoList;
    }

    private Comparator<PostResponseDto> getComparatorForId() {
        return (p1, p2) -> p2.getId() - p1.getId();
    }

    private StringBuilder getStringWithPlaceHolders(int numberOfPlaceHolders) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfPlaceHolders; i++) {
            sb.append("?,");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb;
    }
}
