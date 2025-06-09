package com.til.light_iot_cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.DetectionKeyframe;
import com.til.light_iot_cloud.service.DetectionKeyframeService;
import com.til.light_iot_cloud.service.ImageStorageService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Resource
    private DetectionKeyframeService detectionKeyframeService;

    @GetMapping("/images")
    public ResponseEntity<org.springframework.core.io.Resource> loadImage(
            HttpServletRequest request,
            @RequestParam Long id,
            @RequestParam(required = false, defaultValue = "false") boolean thumb) {
        AuthContext authContext = (AuthContext) request.getAttribute("authContext");
        
        // 验证图片是否属于当前用户
        DetectionKeyframe keyframe = detectionKeyframeService.getOne(
            new LambdaQueryWrapper<DetectionKeyframe>()
                .eq(DetectionKeyframe::getId, id)
                .eq(DetectionKeyframe::getUserId, authContext.getUser().getId())
        );

        if (keyframe == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        org.springframework.core.io.Resource file = thumb ? 
            storageService.loadThumbImage(id.toString() + ".jpg") :
            storageService.loadImage(id.toString() + ".jpg");
            
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(file);
    }
}
