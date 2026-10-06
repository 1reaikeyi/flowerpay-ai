package com.branch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.service.FlowerOrderDetailService;
import com.branch.mapper.FlowerOrderDetailMapper;
import com.branch.domain.entity.FlowerOrderDetail;

import org.springframework.stereotype.Service;

@Service
public class FlowerOrderDetailServiceImpl extends ServiceImpl<FlowerOrderDetailMapper, FlowerOrderDetail> implements FlowerOrderDetailService {

}
