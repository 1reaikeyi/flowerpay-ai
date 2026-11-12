package com.branch.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GeoJSON 坐标点，对应 Redis JSON 中的 location 字段：
 * { "type": "Point", "coordinates": [经度, 纬度] }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoPoint {

    private String type;

    /** 坐标数组，第一个是经度，第二个是纬度 */
    private List<Double> coordinates;

    public Double getLongitude() {
        return this.coordinates != null && this.coordinates.size() >= 2 ? this.coordinates.get(0) : null;
    }

    public Double getLatitude() {
        return this.coordinates != null && this.coordinates.size() >= 2 ? this.coordinates.get(1) : null;
    }
}
