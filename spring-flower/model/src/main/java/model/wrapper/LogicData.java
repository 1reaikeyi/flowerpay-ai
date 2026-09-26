package model.wrapper;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogicData {
    private LocalDateTime expireTime;
    private Object data;
}
