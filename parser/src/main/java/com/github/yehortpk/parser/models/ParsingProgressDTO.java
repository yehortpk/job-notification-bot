package com.github.yehortpk.parser.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ParsingProgressDTO {
    private List<ParserProgress> parsers = new ArrayList<>();
    private boolean finished = false;
    public record ParserProgress(int parserID, List<Integer> steps) {}

    public void addParserProgress(int parserID, List<Integer> steps) {
        parsers.add(new ParserProgress(parserID, steps));
    }
}
