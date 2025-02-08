package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.model.PostTag;
import ru.yandex.practicum.model.Tag;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JdbcBlogRepository implements PostRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public JdbcBlogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addPostDao(PostDao postDao) {
        jdbcTemplate.update("INSERT INTO posts (name, base_64_image, text) VALUES (?, ?, ?)",
                postDao.getName(), postDao.getBase64Image(), postDao.getText());

        int postDaoId = jdbcTemplate.query("""
                SELECT id
                FROM posts
                ORDER BY id DESC
                LIMIT(1);
                """, MAP_TO_ID).get(0);

        postDao.setId(postDaoId);
        addNewTags(postDao);
    }

    @Override
    public void addLike(int postDaoId) {
        jdbcTemplate.update(
                """
                        UPDATE posts 
                        SET number_of_likes = number_of_likes + 1
                        WHERE id = ?
                        """,
                postDaoId
        );
    }

    @Override
    public void addComment(int postId, String commentText) {
        jdbcTemplate.update("INSERT INTO comments (post_id, text) VALUES (?, ?)", postId, commentText);

    }

    @Override
    public List<PostDao> getSortedFeed() {
        List<PostDao> postDaosList = jdbcTemplate.query(
                """
                        SELECT * 
                        FROM posts p
                        ORDER BY id DESC
                        """, MAP_TO_POSTDAO);
        return getPostDaoListWithCommentsAndTags(postDaosList);
    }

    @Override
    public List<PostDao> getFeedWithChosenTags(String tagsInString) {
        List<Integer> postDaosIdsWithTags = jdbcTemplate.query(
                """
                        SELECT post_id
                        FROM posts_tags pt
                        LEFT JOIN tags t ON t.id = pt.tag_id
                        WHERE t.text IN 
                        """ + tagsInString, MAP_TO_POST_ID
        );

        if (postDaosIdsWithTags.isEmpty()) {
            return new ArrayList<>();
        }

        String postDaosIdsInString = mapListIdsToString(postDaosIdsWithTags);
        List<PostDao> selectedPostDaosList = jdbcTemplate.query(
                """
                        SELECT *
                        FROM posts
                        WHERE id IN 
                        """ + postDaosIdsInString, MAP_TO_POSTDAO
        );
        return getPostDaoListWithCommentsAndTags(selectedPostDaosList);
    }

    @Override
    public PostDao getPostById(Long id) {
        String query = "SELECT * FROM posts WHERE id = " + id + ";";
        PostDao postDao = jdbcTemplate.query(query, MAP_TO_POSTDAO).get(0);
        List<Comment> commentList = getAllCommentsForPost(postDao.getId());
        postDao.getCommentsList().addAll(commentList);
        List<String> tagsTextList = getAllTagsTextForPost(postDao.getId());
        postDao.getTagsTextList().addAll(tagsTextList);
        return postDao;
    }

    @Override
    public List<PostDao> getFeedSplittedByPages(int postsOnPage, int pageNumber) {
        List<PostDao> postDaosList = jdbcTemplate.query(
                "SELECT * FROM posts p LIMIT " + postsOnPage + " OFFSET " + postsOnPage * (pageNumber - 1),
                MAP_TO_POSTDAO);
        return getPostDaoListWithCommentsAndTags(postDaosList);
    }

    @Override
    public void changePost(PostDao changedPostDao) {
        jdbcTemplate.update("""
                DELETE FROM posts_tags
                WHERE post_id = ?
                """, changedPostDao.getId());

        jdbcTemplate.update(
                """
                        UPDATE posts 
                        SET name = ?, base_64_image = ?, text = ?
                        WHERE id = ?
                        """,
                changedPostDao.getName(), changedPostDao.getBase64Image(), changedPostDao.getText(), changedPostDao.getId());

        addNewTags(changedPostDao);
        System.out.println();
    }

    @Override
    public void deletePost(Long id) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    private List<String> getAllTagsTextForPost(int postId) {
        List<String> tagsTextList = new ArrayList<>();
        List<PostTag> postsTagsList = getPostsTagsList();
        if (postsTagsList != null && !postsTagsList.isEmpty()) {
            List<Integer> tagsIdsForPostDao = getTagsIdsForPostDao(postId);
            if (tagsIdsForPostDao != null && !tagsIdsForPostDao.isEmpty()) {
                String tagsIdsInStringWithBrackets = mapListIdsToString(tagsIdsForPostDao);
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
    public void deleteComment(Long postDaoId, Long commentId) {
        jdbcTemplate.update("""
                DELETE FROM comments
                WHERE id = ?
                """, commentId);
    }

    private Map<Integer, String> getTagsMap() {
        List<Tag> tagList = jdbcTemplate.query(
                """
                        SELECT *
                        FROM tags
                        """, MAP_TO_TAG);
        Map<Integer, String> tagMap = tagList.stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getText));

        return tagMap;
    }

    private List<PostTag> getPostsTagsList() {
        return jdbcTemplate.query("""
                SELECT *
                FROM posts_tags
                """, MAP_TO_POST_TAG);
    }

    private List<Integer> getTagsIdsForPostDao(int postDaoId) {
        return jdbcTemplate.query("""
                SELECT tag_id
                FROM posts_tags
                WHERE post_id = 
                """ + postDaoId, MAP_TO_TAG_ID);
    }

    private void addNewTags(PostDao postDao) {
        Map<Integer, String> tagMap = getTagsMap();
        for (String tagText : postDao.getTagsTextList()) {
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
                tagId = jdbcTemplate.query(" SELECT id FROM tags WHERE text = " + tagText,
                        MAP_TO_ID).get(0);
            }

            jdbcTemplate.update("""
                            INSERT INTO posts_tags (post_id, tag_id)
                            VALUES (?, ?);
                            """,
                    postDao.getId(), tagId);
        }
    }

    private Map<Integer, List<Comment>> getCommentsMap() {
        Map<Integer, List<Comment>> comments = new HashMap<>();
        List<Comment> commentList = jdbcTemplate.query(
                """
                        SELECT *
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
        List<Comment> commentList = jdbcTemplate.query(
                """
                        SELECT *
                        FROM comments
                        WHERE post_id = 
                        """ + postId, MAP_TO_COMMENTS);

        return commentList;
    }

    private void addTagsToPostDaos(Map<Integer, PostDao> postDaosMap) {
        Map<Integer, String> tagsMap = getTagsMap();
        List<PostTag> postsTagsList = getPostsTagsList();
        for (PostTag pt : postsTagsList) {
            if (postDaosMap.containsKey(pt.getPostId())) {
                postDaosMap.get(pt.getPostId()).getTagsTextList().add(tagsMap.get(pt.getTagId()));
            }
        }
    }

    private void addCommentsToPostDaos(Map<Integer, PostDao> postDaosMap) {
        Map<Integer, List<Comment>> commentsMap = getCommentsMap();
        for (Integer postDaoId : commentsMap.keySet()) {
            if (postDaosMap.containsKey(postDaoId)) {
                postDaosMap.get(postDaoId).getCommentsList().addAll(commentsMap.get(postDaoId));
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


    private List<PostDao> getPostDaoListWithCommentsAndTags(List<PostDao> postDaosList) {
        Map<Integer, PostDao> postDaosMap = postDaosList.stream()
                .collect(Collectors.toMap(PostDao::getId, postDao -> postDao));

        addCommentsToPostDaos(postDaosMap);
        addTagsToPostDaos(postDaosMap);
        return new ArrayList<>(postDaosMap.values());
    }
}
