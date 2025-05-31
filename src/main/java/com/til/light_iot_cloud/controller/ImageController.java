package com.til.light_iot_cloud.controller;

import com.til.light_iot_cloud.service.ImageStorageService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@ResponseBody
public class ImageController {
    @Resource
    private ImageStorageService storageService;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("url") MultipartFile file, RedirectAttributes redirectAttributes) {

    }

    @GetMapping("/image")
    public ResponseEntity<org.springframework.core.io.Resource> serveThumbnail(@RequestParam("url") String filename) {

    }

}
