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
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class ImageStorageServiceImpl implements ImageStorageService {

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
        } catch (IOException e) {
            log.error("Could not initialize storage directory", e);
        }
    }

    @SneakyThrows
    public String storeImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null
                ?
                originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = UUID.randomUUID() + extension;
        String thumbnailFilename = "thumb_" + filename;

        // 存储原始图像
        Path destination = Paths.get(storagePath).resolve(filename);
        Files.copy(file.getInputStream(), destination);

        createScaledThumbnail(file.getBytes(), thumbnailFilename);

        log.info("Stored image: {}", destination.toAbsolutePath());
        return filename;
    }

    private void createScaledThumbnail(byte[] imageData, String thumbnailFilename) throws IOException {
        Path thumbnailPath = Paths.get(storagePath).resolve(thumbnailFilename);

        try (InputStream inputStream = new ByteArrayInputStream(imageData)) {
            // 第一次处理：获取原始图像尺寸
            BufferedImage originalImage = Thumbnails.of(inputStream).asBufferedImage();
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // 计算缩放后的尺寸
            int scaledWidth = (int) (originalWidth * thumbnailWidth);
            int scaledHeight = (int) (originalHeight * thumbnailHeight);


            // 确保最小尺寸为10px
            if (scaledWidth < 10) {
                scaledWidth = 10;
            }

            if (scaledHeight < 10) {
                scaledHeight = 10;
            }

            // 第二次处理：创建缩略图
            try (InputStream inputStream2 = new ByteArrayInputStream(imageData)) {
                Thumbnails.of(inputStream2)
                        .size(scaledWidth, scaledHeight)
                        .outputFormat("jpg") // 统一输出为JPG格式
                        .outputQuality(0.8) // 80%质量
                        .toFile(thumbnailPath.toFile());
            }
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
    public void deleteImage(String filename) {
        Path file = Paths.get(storagePath).resolve(filename);
        Files.deleteIfExists(file);

        // 删除缩略图
        Path thumbnail = Paths.get(storagePath).resolve("thumb_" + filename);
        Files.deleteIfExists(thumbnail);
    }
}
