package com.branch.controller;

import com.branch.domain.vo.HouseResultVo;
import com.branch.service.HouseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地图找房接口
 */
@RequestMapping("/house/search")
@RestController
public class HouseSearchController {

    @Autowired
    private HouseSearchService houseSearchService;

    /**
     * 查询行政区维度的房源均价和套数，供 search.html 绘制地图标注
     */
    @GetMapping
    public List<HouseResultVo> search() {
        return this.houseSearchService.searchDistrict();
    }
}
