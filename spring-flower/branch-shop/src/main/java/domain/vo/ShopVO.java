package domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopVO {

    private Long id;

    /**
     * 商铺名称
     */

    private String name;

    /**
     * 商铺类型的 id
     */

    private Long typeId;

    /**
     * 商铺图片，多个图片以隔开
     */

    private String images;

    /**
     * 商圈，例如陆家嘴
     */

    private String area;

    /**
     * 地址
     */

    private String address;

    /**
     * 经度
     */

    private Double x;

    /**
     * 纬度
     */

    private Double y;

    /**
     * 均价，取整数
     */

    private Long avgPrice;

    /**
     * 销量
     */

    private Long sold;

    /**
     * 评论数量
     */

    private Long comments;

    /**
     * 评分，1~5分，乘10保存，避免小数
     */

    private Long score;

    /**
     * 营业时间，例如 10:00-22:00
     */
    private String openHours;
}
