package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.vo.UserAddressVO;
import common.result.ScrollResult;
import com.branch.domain.dto.UserAddressDTO;
import com.branch.domain.entity.UserAddress;

import java.util.List;

/**
 * 用户地址簿 Service（对应 user_address 表）
 */

public interface UserAddressService extends IService<UserAddress> {

    void deleteAddress(List<Long> ids);
    UserAddressDTO create(UserAddressDTO userAddressDTO);
    UserAddressVO readDefaultAddress();
    ScrollResult<UserAddressVO> readPage(Long offset, Long current);
    void updateDefaultAddress(Long id);
    void updateAddress(UserAddressDTO userAddressDTO);
}
