package com.branch.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ArticlePageDTO {@Min(value = 1, message = "页码不能小于1")
private Long page = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 20, message = "每页条数不能超过20")
    private Long pageSize = 10L;
}
