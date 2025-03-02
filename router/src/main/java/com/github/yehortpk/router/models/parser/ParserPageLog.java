package com.github.yehortpk.router.models.parser;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParserPageLog {
    @Field("log_id")
    private int id;
    @Field("log_level")
    private LogLevelEnum level;
    @Field("log_timestamp")
    private LocalDateTime timestamp;
    @Field("log_message")
    private String message;
}
