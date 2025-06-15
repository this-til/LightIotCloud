package com.til.light_iot_cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.service.DeviceService;
import com.til.light_iot_cloud.mapper.DeviceMapper;
import org.springframework.stereotype.Service;

/**
* @author cat
* @description 针对表【device】的数据库操作Service实现
* @createDate 2025-06-15 16:54:02
*/
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>
    implements DeviceService{

}




