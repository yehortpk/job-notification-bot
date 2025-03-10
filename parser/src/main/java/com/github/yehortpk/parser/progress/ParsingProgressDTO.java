package com.github.yehortpk.parser.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class ParsingProgressDTO {
    private String parsingHash;
    @Builder.Default
    private boolean finished = false;
    @Setter
    private List<ParserProgress> parsers = new ArrayList<>();

    @JsonProperty("total")
    @Builder.Default
    private int parsedVacanciesTotalCount = 0;
    @JsonProperty("new")
    @Builder.Default
    private int newVacanciesTotalCount = 0;
    @JsonProperty("outdated")
    @Builder.Default
    private int outdatedVacanciesTotalCount = 0;
}
