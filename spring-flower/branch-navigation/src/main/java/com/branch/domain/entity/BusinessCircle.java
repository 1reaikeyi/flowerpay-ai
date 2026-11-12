package com.branch.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商圈
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCircle {

    private String id;

    private String name;

    private Integer code;

    private GeoPoint location;

    private Integer districtCode;

    private String lianJiaUrl;
}
