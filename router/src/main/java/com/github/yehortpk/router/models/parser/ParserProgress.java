package com.github.yehortpk.router.models.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParserProgress {
    @Field("parser_id")
    private int id;
    @Field("parser_title")
    private String title;
    @Field("parser_total_pages")
    private int totalPages;

    @Field("parsed_vacancies")
    private int parsedVacanciesCnt;
    @Field("new_vacancies")
    private int newVacanciesCnt;
    @Field("outdated_vacancies")
    private int outdatedVacanciesCnt = 0;


    @Field("parser_pages")
    private List<ParserPageProgress> pages;



}
