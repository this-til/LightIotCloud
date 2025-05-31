package com.til.light_iot_cloud.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName light_data
 */
@TableName(value ="light_data")
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
    private LocalDateTime time;

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
    private Double pm25;

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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        LightData other = (LightData) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getLightId() == null ? other.getLightId() == null : this.getLightId().equals(other.getLightId()))
            && (this.getTime() == null ? other.getTime() == null : this.getTime().equals(other.getTime()))
            && (this.getHumidity() == null ? other.getHumidity() == null : this.getHumidity().equals(other.getHumidity()))
            && (this.getTemperature() == null ? other.getTemperature() == null : this.getTemperature().equals(other.getTemperature()))
            && (this.getPm10() == null ? other.getPm10() == null : this.getPm10().equals(other.getPm10()))
            && (this.getPm25() == null ? other.getPm25() == null : this.getPm25().equals(other.getPm25()))
            && (this.getIllumination() == null ? other.getIllumination() == null : this.getIllumination().equals(other.getIllumination()))
            && (this.getWindSpeed() == null ? other.getWindSpeed() == null : this.getWindSpeed().equals(other.getWindSpeed()))
            && (this.getWindDirection() == null ? other.getWindDirection() == null : this.getWindDirection().equals(other.getWindDirection()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getLightId() == null) ? 0 : getLightId().hashCode());
        result = prime * result + ((getTime() == null) ? 0 : getTime().hashCode());
        result = prime * result + ((getHumidity() == null) ? 0 : getHumidity().hashCode());
        result = prime * result + ((getTemperature() == null) ? 0 : getTemperature().hashCode());
        result = prime * result + ((getPm10() == null) ? 0 : getPm10().hashCode());
        result = prime * result + ((getPm25() == null) ? 0 : getPm25().hashCode());
        result = prime * result + ((getIllumination() == null) ? 0 : getIllumination().hashCode());
        result = prime * result + ((getWindSpeed() == null) ? 0 : getWindSpeed().hashCode());
        result = prime * result + ((getWindDirection() == null) ? 0 : getWindDirection().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", lightId=").append(lightId);
        sb.append(", time=").append(time);
        sb.append(", humidity=").append(humidity);
        sb.append(", temperature=").append(temperature);
        sb.append(", pm10=").append(pm10);
        sb.append(", pm25=").append(pm25);
        sb.append(", illumination=").append(illumination);
        sb.append(", windSpeed=").append(windSpeed);
        sb.append(", windDirection=").append(windDirection);
        sb.append("]");
        return sb.toString();
    }
}