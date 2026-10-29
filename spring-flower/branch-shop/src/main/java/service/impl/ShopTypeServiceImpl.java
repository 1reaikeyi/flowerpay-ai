package service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.entity.ShopType;
import mapper.ShopTypeMapper;

import org.springframework.stereotype.Service;
import service.ShopTypeService;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements ShopTypeService {
    
}
