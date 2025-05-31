package com.til.light_iot_cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.til.light_iot_cloud.data.Car;
import com.til.light_iot_cloud.service.CarService;
import com.til.light_iot_cloud.mapper.CarMapper;
import org.springframework.stereotype.Service;

/**
* @author cat
* @description 针对表【car】的数据库操作Service实现
* @createDate 2025-05-30 20:15:59
*/
@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car>
    implements CarService{

}




