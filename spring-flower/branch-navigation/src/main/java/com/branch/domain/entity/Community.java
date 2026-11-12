package com.branch.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小区
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Community {

    private String id;

    private String name;

    private GeoPoint location;

    private Integer districtCode;

    private Integer businessCircleCode;

    private String lianJiaUrl;
}
