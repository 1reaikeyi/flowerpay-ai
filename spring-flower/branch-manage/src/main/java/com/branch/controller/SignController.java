package com.branch.controller;

import com.branch.domain.dto.TimeDTO;
import com.branch.service.SignService;
import common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/sign")
public class SignController {
    @Autowired
    private SignService signService;

    @PostMapping("/today")
    public Result createSign() {
        return Result.success(signService.sign());
    }

    @PutMapping("/back")
    public Result backSign(@RequestBody @Validated TimeDTO time) {
        return Result.success(signService.backSign(time));
    }

    @PostMapping("/count/of/Month")
    public Result CountSign() {
        return Result.success(signService.countOfMonth());
    }

    @PostMapping("/count/of/time")
    public Result Sign(@RequestBody TimeDTO time) {
        return Result.success(signService.countOfTime(time));
    }

}
