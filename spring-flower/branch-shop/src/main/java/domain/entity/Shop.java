package domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商店表 - 对应数据库 shop 表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("shop")
@Builder
public class Shop implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商铺名称
     */
    @TableField("name")
    private String name;

    /**
     * 商铺类型的 id
     */
    @TableField("type_id")
    private Long typeId;

    /**
     * 商铺图片，多个图片以隔开
     */
    @TableField("images")
    private String images;

    /**
     * 商圈，例如陆家嘴
     */
    @TableField("area")
    private String area;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 经度
     */
    @TableField("x")
    private Double x;

    /**
     * 纬度
     */
    @TableField("y")
    private Double y;

    /**
     * 均价，取整数
     */
    @TableField("avg_price")
    private Long avgPrice;

    /**
     * 销量
     */
    @TableField("sold")
    private Long sold;

    /**
     * 评论数量
     */
    @TableField("comments")
    private Long comments;

    /**
     * 评分，1~5分，乘10保存，避免小数
     */
    @TableField("score")
    private Long score;

    /**
     * 营业时间，例如 10:00-22:00
     */
    @TableField("open_hours")
    private String openHours;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
