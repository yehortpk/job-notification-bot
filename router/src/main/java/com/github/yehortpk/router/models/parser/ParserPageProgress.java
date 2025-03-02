package com.github.yehortpk.router.models.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParserPageProgress {
    @Field("page_id")
    private int id;
    @Field("page_status")
    private PageProgressStatusEnum status;
    @Field("page_parsed_vacancies")
    private int parsedVacanciesCnt;
    @Field("page_logs")
    private List<ParserPageLog> logs = new ArrayList<>();
}
