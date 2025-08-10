package com.til.light_iot_cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.DetectionKeyframe;
import com.til.light_iot_cloud.data.LightState;
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

import java.util.List;

/**
 * 图像资源控制器
 * <p>
 * 负责处理图像资源的访问和下载，包括检测关键帧图像的原图和缩略图。
 * 所有图像访问都需要通过身份验证，确保用户只能访问属于自己的图像资源。
 * <p>
 * 主要功能：
 * - 提供检测关键帧图像的安全访问
 * - 支持原图和缩略图两种格式
 * - 实现基于用户权限的图像访问控制
 * - 返回标准的 HTTP 图像响应
 * <p>
 * 安全机制：
 * - 验证图像所有权，防止越权访问
 * - 支持身份认证上下文
 * - 返回适当的 HTTP 状态码
 * 
 * @author TIL
 */
@Controller
@RequiredArgsConstructor
@ResponseBody
public class ImageController {
    @Resource
    private ImageStorageService storageService;

    @Resource
    private DetectionKeyframeService detectionKeyframeService;

    /**
     * 加载图像资源
     * <p>
     * 根据检测关键帧ID加载对应的图像文件，支持原图和缩略图两种格式。
     * 在返回图像前会验证当前用户是否有权限访问该图像，确保数据安全。
     * <p>
     * 访问控制：
     * - 验证图像关键帧是否存在
     * - 检查图像是否属于当前认证用户
     * - 对于无权限访问返回 403 Forbidden
     * 
     * @param request HTTP 请求对象，用于获取认证上下文
     * @param id 检测关键帧ID，用于定位具体的图像资源
     * @param thumb 是否返回缩略图，默认为 false（返回原图）
     * @return 图像资源的 HTTP 响应，包含 JPEG 格式的图像数据
     *         - 200 OK: 成功返回图像
     *         - 403 Forbidden: 无权限访问该图像
     */
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
