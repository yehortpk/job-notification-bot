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
    int id;
    String title;
    int totalPages;
    List<ParserPageProgress> pages;

    MetadataStatusEnum metadataStatus;

    int parsedVacanciesCnt;
    int newVacanciesCnt;

}
