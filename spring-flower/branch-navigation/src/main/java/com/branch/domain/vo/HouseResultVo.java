package com.branch.domain.vo;


import lombok.Data;

@Data
public class HouseResultVo {


    private String code; //编号：行政区返回行政区 code（如 1016）

    private String name; //显示的名称，可能是区、商圈、小区
    private String price; //均价
    private String total; //房源数量
    private Double longitude; //位置经度
    private Double latitude; //位置维度

}
