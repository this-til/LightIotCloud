package com.til.light_iot_cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.til.light_iot_cloud.data.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author cat
* @description 针对表【user(用户信息表)】的数据库操作Mapper
* @createDate 2025-05-31 13:35:05
* @Entity com.til.light_iot_cloud.opjo.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




