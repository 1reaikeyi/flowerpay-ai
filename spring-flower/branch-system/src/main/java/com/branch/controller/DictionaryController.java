package com.branch.controller;

import framework.system.NumberEnum;
import framework.system.ObjectEnum;
import framework.system.OptionEnum;
import framework.result.Result;
import lombok.extern.slf4j.Slf4j;
import framework.system.DictionaryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dictionary")
@Slf4j
public class DictionaryController {
    @GetMapping("/object")
    public Result<List<DictionaryVO>> dictionary() {
        List<DictionaryVO> list = Arrays.stream(ObjectEnum.values())
                .map(e -> new DictionaryVO(e.name(), e.getName()))
                .collect(Collectors.toList());
        return Result.success(list);
    }
    @GetMapping("/option")
    public Result<List<DictionaryVO>> option() {
        List<DictionaryVO> list = Arrays.stream(OptionEnum.values())
                .map(e -> new DictionaryVO(e.name(), e.getName()))
                .collect(Collectors.toList());
        return Result.success(list);
    }
    @GetMapping("/number")
    public Result<List<DictionaryVO>> number() {
        List<DictionaryVO> list = Arrays.stream(NumberEnum.values())
                .map(e -> new DictionaryVO(e.name(), e.getMeaning()))
                .collect(Collectors.toList());
        return Result.success(list);
    }
}
