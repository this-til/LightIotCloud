package com.til.light_iot_cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.til.light_iot_cloud.data.Detection;
import com.til.light_iot_cloud.service.DetectionService;
import com.til.light_iot_cloud.mapper.DetectionMapper;
import org.springframework.stereotype.Service;

/**
* @author cat
* @description 针对表【detection】的数据库操作Service实现
* @createDate 2025-05-31 17:40:23
*/
@Service
public class DetectionServiceImpl extends ServiceImpl<DetectionMapper, Detection>
    implements DetectionService{

}




