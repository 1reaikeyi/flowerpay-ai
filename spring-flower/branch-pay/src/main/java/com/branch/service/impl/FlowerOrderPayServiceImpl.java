package com.branch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.service.FlowerOrderPayService;
import com.branch.mapper.FlowerOrderPayMapper;
import com.branch.domain.entity.FlowerOrderPay;
import org.springframework.stereotype.Service;

@Service
public class FlowerOrderPayServiceImpl extends ServiceImpl<FlowerOrderPayMapper, FlowerOrderPay> implements FlowerOrderPayService {
}
