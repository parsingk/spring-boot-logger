package com.spring.boot.logger;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LogDTO implements ILogDTO {
    private String name;
    private Integer code;
}
