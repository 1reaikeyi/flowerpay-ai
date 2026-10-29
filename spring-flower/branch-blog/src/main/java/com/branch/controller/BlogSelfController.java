package com.branch.controller;

import com.branch.domain.dto.BlogSelfDTO;
import com.branch.result.Result;
import com.branch.service.BlogSelfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blog/self")
@Slf4j
public class BlogSelfController {
    @Autowired
    private BlogSelfService blogSelfService;
    @PutMapping
    public Result updateSelf(BlogSelfDTO blogSelfDTO){
        blogSelfService.updateSelf(blogSelfDTO);
        return Result.success(blogSelfDTO.getUserId());
    }
}
