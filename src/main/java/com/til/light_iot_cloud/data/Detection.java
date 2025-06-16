package com.til.light_iot_cloud.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.til.light_iot_cloud.data.input.DetectionItemInput;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @TableName detection
 */
@TableName(value = "detection")
@Data
public class Detection {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Long keyframeId;

    /**
     *
     */
    private Long itemId;

    /**
     *
     */
    private Double x;

    /**
     *
     */
    private Double y;

    /**
     *
     */
    private Double w;

    /**
     *
     */
    private Double h;

    /**
     *
     */
    private Double probability;

    @Nullable
    @TableField(exist = false)
    private String model;

    @Nullable
    @TableField(exist = false)
    private String item;


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
        Detection other = (Detection) that;
        return (this.getId() == null
                ? other.getId() == null
                : this.getId().equals(other.getId()))
                && (this.getKeyframeId() == null
                ? other.getKeyframeId() == null
                : this.getKeyframeId().equals(other.getKeyframeId()))
                && (this.getItemId() == null
                ? other.getItemId() == null
                : this.getItemId().equals(other.getItemId()))
                && (this.getX() == null
                ? other.getX() == null
                : this.getX().equals(other.getX()))
                && (this.getY() == null
                ? other.getY() == null
                : this.getY().equals(other.getY()))
                && (this.getW() == null
                ? other.getW() == null
                : this.getW().equals(other.getW()))
                && (this.getH() == null
                ? other.getH() == null
                : this.getH().equals(other.getH()))
                && (this.getProbability() == null
                ? other.getProbability() == null
                : this.getProbability().equals(other.getProbability()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null)
                ? 0
                : getId().hashCode());
        result = prime * result + ((getKeyframeId() == null)
                ? 0
                : getKeyframeId().hashCode());
        result = prime * result + ((getItemId() == null)
                ? 0
                : getItemId().hashCode());
        result = prime * result + ((getX() == null)
                ? 0
                : getX().hashCode());
        result = prime * result + ((getY() == null)
                ? 0
                : getY().hashCode());
        result = prime * result + ((getW() == null)
                ? 0
                : getW().hashCode());
        result = prime * result + ((getH() == null)
                ? 0
                : getH().hashCode());
        result = prime * result + ((getProbability() == null)
                ? 0
                : getProbability().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", keyframeId=").append(keyframeId);
        sb.append(", itemId=").append(itemId);
        sb.append(", x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", w=").append(w);
        sb.append(", h=").append(h);
        sb.append(", probability=").append(probability);
        sb.append("]");
        return sb.toString();
    }
}