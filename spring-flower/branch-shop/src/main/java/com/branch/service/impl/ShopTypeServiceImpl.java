package com.branch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.model.entity.ShopType;
import com.branch.service.mapper.ShopTypeMapper;

import org.springframework.stereotype.Service;
import com.branch.service.ShopTypeService;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements ShopTypeService {
    
}
