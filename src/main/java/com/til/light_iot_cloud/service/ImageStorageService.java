package com.til.light_iot_cloud.service;

import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    void storeImage(MultipartFile file,String name);
    void storeImage(byte[] bytes, String name);

    Resource loadImage(String filename);
    Resource loadThumbImage(String filename);

    void deleteImage(String filename);
}
