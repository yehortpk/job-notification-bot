package com.github.yehortpk.router.models.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParserProgress {
    private int id;
    private String title;
    private int totalPages;
    private List<ParserPageProgress> pages;

    private MetadataStatusEnum metadataStatus;

    private int parsedVacanciesCnt;
    private int newVacanciesCnt;
    private int outdatedVacanciesCnt;

}
