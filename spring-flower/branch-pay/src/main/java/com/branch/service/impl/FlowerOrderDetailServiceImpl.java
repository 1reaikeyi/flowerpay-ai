package com.branch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.service.FlowerOrderDetailService;
import com.branch.service.mapper.FlowerOrderDetailMapper;
import com.branch.model.entity.FlowerOrderDetail;

import org.springframework.stereotype.Service;

@Service
public class FlowerOrderDetailServiceImpl extends ServiceImpl<FlowerOrderDetailMapper, FlowerOrderDetail> implements FlowerOrderDetailService {

}
