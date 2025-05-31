package com.til.light_iot_cloud.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    String storeImage(MultipartFile file);

    Resource loadImage(String filename);

    void deleteImage(String filename);
}
