package com.til.light_iot_cloud.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

/**
 * @TableName light_data
 */
@TableName(value = "light_data")
@Data
public class LightData {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Long lightId;

    /**
     *
     */
    private OffsetDateTime time;

    /**
     * 温度
     */
    private Double humidity;

    /**
     * 温度
     */
    private Double temperature;

    /**
     * PM10
     */
    private Double pm10;

    /**
     * PM2.5
     */
    @TableField(value = "pm2_5")
    private Double pm25;

    @TableField(exist = false)
    private Double pm2_5;

    /**
     * 光照
     */
    private Double illumination;

    /**
     * 风速
     */
    private Double windSpeed;

    /**
     * 风向
     */
    private Double windDirection;

}