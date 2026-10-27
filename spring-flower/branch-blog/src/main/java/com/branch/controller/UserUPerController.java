package com.branch.controller;

import com.branch.domain.dto.UserUPerDTO;
import com.branch.result.Result;
import com.branch.service.UserUPerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blog")
@Slf4j
public class UserUPerController {
    @Autowired
    private UserUPerService userUPerService;
    @PutMapping
    public Result updateSelf(UserUPerDTO userUPerDTO){
        userUPerService.updateSelf(userUPerDTO);
        return Result.success(userUPerDTO.getUserId());
    }
}
