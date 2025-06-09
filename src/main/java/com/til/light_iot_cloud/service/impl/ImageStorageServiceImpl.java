package com.til.light_iot_cloud.service.impl;

import com.til.light_iot_cloud.service.ImageStorageService;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    public static final String thumb = "thumb";

    @Value("${image.storage-path}")
    private String storagePath;

    @Value("${image.thumbnail.width}")
    private float thumbnailWidth;

    @Value("${image.thumbnail.height}")
    private float thumbnailHeight;


    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(storagePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created image storage directory: {}", path.toAbsolutePath());
            }
            Path thumbPath = Paths.get(storagePath).resolve(thumb);
            if (!Files.exists(thumbPath)) {
                Files.createDirectories(thumbPath);
                log.info("Created image storage directory: {}", thumbPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Could not initialize storage directory", e);
        }
    }

    @Override
    @Transactional
    @SneakyThrows
    public void storeImage(MultipartFile file, String name) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        storeImage(file.getBytes(), name);
    }

    @Transactional
    @SneakyThrows
    @Override
    public void storeImage(byte[] bytes, String name) {
        String filename = name + ".jpg";
        String thumbnailFilename = thumb + "/" + filename;

        // 存储原始图像
        Path destination = Paths.get(storagePath).resolve(filename);
        Files.copy(new ByteArrayInputStream(bytes), destination, StandardCopyOption.REPLACE_EXISTING);

        createScaledThumbnail(bytes, thumbnailFilename);

        log.info("Stored image: {}", destination.toAbsolutePath());
    }

    private void createScaledThumbnail(byte[] imageData, String thumbnailFilename) throws IOException {
        Path thumbnailPath = Paths.get(storagePath).resolve(thumbnailFilename);
        try (InputStream inputStream = new ByteArrayInputStream(imageData)) {
            Thumbnails.of(inputStream)
                    .scale(thumbnailWidth, thumbnailHeight)
                    .outputFormat("jpg")
                    .toFile(thumbnailPath.toFile());
        }
    }


    @SneakyThrows
    public Resource loadImage(String filename) {
        Path file = Paths.get(storagePath).resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        }
        throw new RuntimeException("Could not read file: " + filename);
    }

    @SneakyThrows
    public Resource loadThumbImage(String filename) {
        Path file = Paths.get(storagePath).resolve(thumb).resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        }
        throw new RuntimeException("Could not read file: " + filename);
    }

    @Transactional
    @SneakyThrows
    public void deleteImage(String filename) {
        Path file = Paths.get(storagePath).resolve(filename);
        Files.deleteIfExists(file);

        // 删除缩略图
        Path thumbnail = Paths.get(storagePath).resolve("thumb_" + filename);
        Files.deleteIfExists(thumbnail);
    }
}
