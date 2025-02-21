package ru.yandex.practicum.repository;

import ru.yandex.practicum.dto.PostRequestDto;
import ru.yandex.practicum.dto.PostResponseDto;

import java.sql.SQLException;
import java.util.List;

public interface LikeRepository {
    void addLike(int postDtoId);
}
