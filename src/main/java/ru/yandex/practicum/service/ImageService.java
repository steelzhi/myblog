package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.PostResponseDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
@AllArgsConstructor
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public byte[] getImage(int postDtoId) {
        return imageRepository.getImage(postDtoId);
    }
}
