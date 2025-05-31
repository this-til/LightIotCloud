package com.til.light_iot_cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.til.light_iot_cloud.data.Light;
import com.til.light_iot_cloud.service.LightService;
import com.til.light_iot_cloud.mapper.LightMapper;
import org.springframework.stereotype.Service;

/**
* @author cat
* @description 针对表【light】的数据库操作Service实现
* @createDate 2025-05-30 20:16:17
*/
@Service
public class LightServiceImpl extends ServiceImpl<LightMapper, Light>
    implements LightService{

}




