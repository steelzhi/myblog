package ru.yandex.practicum.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.PostTag;
import ru.yandex.practicum.model.Tag;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class JdbcBlogRepository implements PostRepository {
    RowMapper<PostDto> MAP_TO_POSTDTO = (ResultSet resultSet, int rowNum) -> new PostDto(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getString("base_64_image"),
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

    RowMapper<PostTag> MAP_TO_POST_TAG = (ResultSet resultSet, int rowNum) -> new PostTag(
            resultSet.getInt("id"),
            resultSet.getInt("post_id"),
            resultSet.getInt("tag_id"));

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public JdbcBlogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PostDto addPostDto(PostDto postDto) {
        jdbcTemplate.update("INSERT INTO posts (name, base_64_image, text) VALUES (?, ?, ?)",
                postDto.getName(), postDto.getBase64Image(), postDto.getText());

        int postDtoId = jdbcTemplate.query("""
                SELECT id
                FROM posts
                ORDER BY id DESC
                LIMIT(1);
                """, MAP_TO_ID).get(0);

        postDto.setId(postDtoId);
        addNewTags(postDto);
        return postDto;
    }

    @Override
    public PostDto addLike(int postDtoId) {
        jdbcTemplate.update(
                """
                        UPDATE posts 
                        SET number_of_likes = number_of_likes + 1
                        WHERE id = ?
                        """,
                postDtoId
        );

        return getPostById(postDtoId);
    }

    @Override
    public PostDto addComment(int postDtoId, String commentText) {
        jdbcTemplate.update("INSERT INTO comments (post_id, text) VALUES (?, ?)", postDtoId, commentText);

        return getPostById(postDtoId);
    }

    @Override
    public List<PostDto> getSortedFeed() {
        List<PostDto> postDtosList = jdbcTemplate.query(
                """
                        SELECT id, name, base_64_image, text, number_of_likes 
                        FROM posts p
                        """, MAP_TO_POSTDTO);
        return getPostDtoListWithCommentsAndTags(postDtosList);
    }

    @Override
    public List<PostDto> getFeedWithChosenTags(String tagsInString) {
        List<Integer> postDtosIdsWithTags = jdbcTemplate.query(
                "SELECT post_id FROM posts_tags pt LEFT JOIN tags t ON t.id = pt.tag_id WHERE t.text IN "
                        + tagsInString + " ORDER BY post_id DESC", MAP_TO_POST_ID
        );

        if (postDtosIdsWithTags.isEmpty()) {
            return new ArrayList<>();
        }

        String postDtosIdsInString = mapListIdsToString(postDtosIdsWithTags);
        List<PostDto> selectedPostDtosList = jdbcTemplate.query(
                """
                        SELECT id, name, base_64_image, text, number_of_likes 
                        FROM posts
                        WHERE id IN 
                        """ + postDtosIdsInString, MAP_TO_POSTDTO
        );
        return getPostDtoListWithCommentsAndTags(selectedPostDtosList);
    }

    @Override
    public PostDto getPostById(int id) {
        String query = "SELECT id, name, base_64_image, text, number_of_likes FROM posts WHERE id = " + id + ";";
        PostDto postDto = jdbcTemplate.query(query, MAP_TO_POSTDTO).get(0);
        List<Comment> commentList = getAllCommentsForPost(postDto.getId());
        postDto.getCommentsList().addAll(commentList);
        List<String> tagsTextList = getAllTagsTextForPost(postDto.getId());
        postDto.getTagsTextList().addAll(tagsTextList);
        return postDto;
    }

    /*
    * Игорь, по этому методу Вы оставили такой комментарий: "Метод getFeedSplittedByPages нужен только чтобы
    * получить размер". Не смог его понять - этот метод же у меня выбирает из БД посты (только те, которые будут
    * отображены согласно параметрам пагинации).
     */
    @Override
    public List<PostDto> getFeedSplittedByPages(int postsOnPage, int pageNumber) {
        List<PostDto> postDtosList = jdbcTemplate.query(
                "SELECT id, name, base_64_image, text, number_of_likes FROM posts p ORDER BY id DESC LIMIT "
                        + postsOnPage + " OFFSET " + postsOnPage * (pageNumber - 1),
                MAP_TO_POSTDTO);
        return getPostDtoListWithCommentsAndTags(postDtosList);
    }

    @Override
    public PostDto changePost(PostDto changedPostDto) {
        jdbcTemplate.update("""
                DELETE FROM posts_tags
                WHERE post_id = ?
                """, changedPostDto.getId());

        jdbcTemplate.update(
                """
                        UPDATE posts 
                        SET name = ?, base_64_image = ?, text = ?
                        WHERE id = ?
                        """,
                changedPostDto.getName(),
                changedPostDto.getBase64Image(),
                changedPostDto.getText(),
                changedPostDto.getId());

        addNewTags(changedPostDto);
        return getPostById(changedPostDto.getId());
    }

    @Override
    public PostDto changeComment(int id, int postId, String text) {
        jdbcTemplate.update("UPDATE comments SET text = ? WHERE id = ? AND post_id = ?", text, id, postId);
        return getPostById(postId);
    }

    @Override
    public void deletePost(int id) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    private List<String> getAllTagsTextForPost(int postId) {
        List<String> tagsTextList = new ArrayList<>();
        List<PostTag> postsTagsList = getPostsTagsList();
        if (postsTagsList != null && !postsTagsList.isEmpty()) {
            List<Integer> tagsIdsForPostDto = getTagsIdsForPostDto(postId);
            if (tagsIdsForPostDto != null && !tagsIdsForPostDto.isEmpty()) {
                String tagsIdsInStringWithBrackets = mapListIdsToString(tagsIdsForPostDto);
                tagsTextList = jdbcTemplate.query("""
                        SELECT text
                        FROM tags
                        WHERE id IN 
                        """ + tagsIdsInStringWithBrackets, MAP_TO_TEXT);
            }
        }

        return tagsTextList;
    }

    @Override
    public PostDto deleteComment(int postDtoId, int commentId) {
        jdbcTemplate.update("""
                DELETE FROM comments
                WHERE id = ?
                """, commentId);
        return getPostById(postDtoId);
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
        return jdbcTemplate.query("""
                SELECT tag_id
                FROM posts_tags
                WHERE post_id = 
                """ + postDtoId, MAP_TO_TAG_ID);
    }

    private void addNewTags(PostDto postDto) {
        Map<Integer, String> tagMap = getTagsMap();
        for (String tagText : postDto.getTagsTextList()) {
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
                        """, MAP_TO_ID).get(0);
            } else {
                tagId = jdbcTemplate.query("SELECT id FROM tags WHERE text = '" + tagText + "'",
                        MAP_TO_ID).get(0);
            }

            jdbcTemplate.update("""
                            INSERT INTO posts_tags (post_id, tag_id)
                            VALUES (?, ?);
                            """,
                    postDto.getId(), tagId);
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

    private List<Comment> getAllCommentsForPost(long postId) {
        List<Comment> commentList = jdbcTemplate.query(
                """
                        SELECT id, text, post_id
                        FROM comments
                        WHERE post_id = 
                        """ + postId, MAP_TO_COMMENTS);

        return commentList;
    }

    private void addTagsToPostDtos(Map<Integer, PostDto> postDtosMap) {
        Map<Integer, String> tagsMap = getTagsMap();
        List<PostTag> postsTagsList = getPostsTagsList();
        for (PostTag pt : postsTagsList) {
            if (postDtosMap.containsKey(pt.getPostId())) {
                postDtosMap.get(pt.getPostId()).getTagsTextList().add(tagsMap.get(pt.getTagId()));
            }
        }
    }

    private void addCommentsToPostDtos(Map<Integer, PostDto> postDtosMap) {
        Map<Integer, List<Comment>> commentsMap = getCommentsMap();
        for (Integer postDtoId : commentsMap.keySet()) {
            if (postDtosMap.containsKey(postDtoId)) {
                postDtosMap.get(postDtoId).getCommentsList().addAll(commentsMap.get(postDtoId));
            }
        }
    }

    private String mapListIdsToString(List<Integer> idsList) {
        StringBuilder sb = new StringBuilder("('");
        for (Integer id : idsList) {
            sb.append(id).append("','");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");
        return sb.toString();
    }


    private List<PostDto> getPostDtoListWithCommentsAndTags(List<PostDto> postDtosList) {
        Map<Integer, PostDto> postDtosMap = postDtosList.stream()
                .collect(Collectors.toMap(PostDto::getId, postDto -> postDto));

        addCommentsToPostDtos(postDtosMap);
        addTagsToPostDtos(postDtosMap);
        List<PostDto> postDtoList = new ArrayList<>(postDtosMap.values());
        postDtoList.sort(getComparatorForId());
        return postDtoList;
    }

    private Comparator<PostDto> getComparatorForId() {
        return (p1, p2) -> p2.getId() - p1.getId();
    }
}
