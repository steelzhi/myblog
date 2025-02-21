package ru.yandex.practicum.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.PostTag;
import ru.yandex.practicum.model.Tag;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class JdbcImageRepository implements ImageRepository {
    private RowMapper<byte[]> MAP_TO_IMAGE = (ResultSet resultSet, int rowNum) -> resultSet.getBytes("image");

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
}
