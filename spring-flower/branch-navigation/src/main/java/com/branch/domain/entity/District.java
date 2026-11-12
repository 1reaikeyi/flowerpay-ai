package com.branch.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行政区
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class District {

    private String id;

    private String name;

    private Integer code;

    private GeoPoint location;
}
