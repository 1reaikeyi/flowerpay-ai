package com.branch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeDTO {
    @NonNull
    private String startTime;
    private String endTime;
}
