package com.branch.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 房源
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class House {

    private String id;

    private String title;

    private Double price;

    private GeoPoint location;

    private Integer districtCode;

    private String communityId;

    private Integer businessCircleCode;

    private Long created;
}
