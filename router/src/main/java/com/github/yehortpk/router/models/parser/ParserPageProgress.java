package com.github.yehortpk.router.models.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParserPageProgress {
    private int id;
    private PageProgressStatusEnum status;
    private int parsedVacanciesCnt;
    private List<ParserPageLog> logs = new ArrayList<>();
}
