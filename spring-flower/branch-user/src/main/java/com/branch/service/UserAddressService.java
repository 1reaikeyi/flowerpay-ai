package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.model.vo.UserAddressVO;
import com.branch.result.ScrollPart;
import com.branch.model.dto.UserAddressDTO;
import com.branch.model.entity.UserAddress;

import java.util.List;

/**
 * 用户地址簿 Service（对应 user_address 表）
 */

public interface UserAddressService extends IService<UserAddress> {

    void deleteAddress(List<Long> ids);
    UserAddressDTO create(UserAddressDTO userAddressDTO);
    UserAddressVO readDefaultAddress();
    ScrollPart<UserAddressVO> readPage(Long offset, Long current);
    void updateDefaultAddress(Long id);
    void updateAddress(UserAddressDTO userAddressDTO);
}
