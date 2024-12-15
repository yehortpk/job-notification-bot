package com.github.yehortpk.parser.models;

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
    private int parsedVacanciesCnt = 0;
    private int newVacanciesCnt = 0;
    private int outdatedVacanciesCnt = 0;

    public record ParserProgress(int parserID, String parserTitle, List<Integer> steps) {}
}
