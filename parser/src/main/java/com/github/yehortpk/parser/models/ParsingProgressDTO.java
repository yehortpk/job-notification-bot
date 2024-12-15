package com.github.yehortpk.parser.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class ParsingProgressDTO {
    @Setter
    private List<ParserProgress> parsers = new ArrayList<>();
    private boolean finished = false;
    @JsonProperty("total")
    private int parsedVacanciesCnt = 0;
    @JsonProperty("new")
    private int newVacanciesCnt = 0;
    @JsonProperty("outdated")
    private int outdatedVacanciesCnt = 0;

    public record ParserProgress(int parserID, String parserTitle, List<Integer> steps) {}
}
