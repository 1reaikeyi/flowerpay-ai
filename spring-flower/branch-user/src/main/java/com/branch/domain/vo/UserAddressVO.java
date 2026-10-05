package com.branch.domain.vo;

import lombok.Data;

@Data
public class UserAddressVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 收花人姓名
     */
    private String consignee;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 收花人性别
     */
    private String sex;

    /**
     * 省级区划编号
     */

    private String provinceCode;

    /**
     * 省级名称
     */

    private String provinceName;

    /**
     * 市级区划编号
     */

    private String cityCode;

    /**
     * 市级名称
     */

    private String cityName;

    /**
     * 区级区划编号
     */

    private String districtCode;

    /**
     * 区级名称
     */

    private String districtName;

    /**
     * 详细地址
     */

    private String detail;

    /**
     * 标签
     */

    private String label;

    /**
     * 是否默认：0否 1是
     */

    private Long isDefault;
}
