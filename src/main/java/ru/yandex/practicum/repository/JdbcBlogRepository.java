package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dao.PostDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcBlogRepository implements PostRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public JdbcBlogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addPost(PostDao postDao) {
        jdbcTemplate.update("INSERT INTO posts (name, text, tags) VALUES (?, ?, ?)",
                postDao.getName(), postDao.getText(), postDao.getTags());
/*        jdbcTemplate.update("INSERT INTO posts (name, base_64_image, text, tags) VALUES (?, ?, ?, ?)",
                postDao.getName(), postDao.getBase64Image(), postDao.getText(), postDao.getTags());*/
    }

    @Override
    public List<PostDao> getSortedFeed() {
        List<PostDao> postDaos = jdbcTemplate.query(
                """
                        SELECT * 
                        FROM posts p
                        ORDER BY id DESC
                        """, MAP_TO_POST);
        Map<Integer, List<String>> comments = getAllComments();
        for (Integer key : comments.keySet()) {
            postDaos.get(key - 1).getComments().addAll(comments.get(key));
        }

        return postDaos;
    }

    @Override
    public void deletePost(Long id) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    @Override
    public PostDao getPostById(Long id) {
        String query = "SELECT * FROM posts WHERE id = " + id + ";";
        PostDao postDao = jdbcTemplate.query(query, MAP_TO_POST).get(0);
        List<String> commentList = getAllCommentsForPost(postDao.getId());
        postDao.getComments().addAll(commentList);
        return postDao;
    }

    private Map<Integer, List<String>> getAllComments() {
        Map<Integer, List<String>> comments = new HashMap<>();
        List<Comment> commentList = jdbcTemplate.query(
                """
                        SELECT post_id, text
                        FROM comments
                        """, MAP_TO_COMMENTS);
        for (Comment c : commentList) {
            if (!comments.containsKey(c.getPostId())) {
                comments.put(c.getPostId(), new ArrayList<>());
            }
            comments.get(c.getPostId()).add(c.getComment());
        }

        return comments;
    }

    private List<String> getAllCommentsForPost(int postId) {
        List<String> commentList = jdbcTemplate.query(
                """
                        SELECT text
                        FROM comments
                        WHERE post_id = 
                        """ + postId, MAP_TO_COMMENT);

        return commentList;
    }

    /*    private void insertTag(String tag, int postId) {
        jdbcTemplate.execute("INSERT INTO tags (text, post_id) VALUES + (" + tag + ", " + postId + ");");
    }*/

/*    private int insertPostAndGetHisId(String query) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, query);
            return ps;
        }, keyHolder);

        return (int) keyHolder.getKey();
    }*/
}
