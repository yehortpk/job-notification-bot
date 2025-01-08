package com.github.yehortpk.router.models.parser;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParserPageLog {
    private int pageID;
    private LogLevelEnum level;
    private LocalDateTime timestamp;
    private String message;
}
