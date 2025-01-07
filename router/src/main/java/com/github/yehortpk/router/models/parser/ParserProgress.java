package com.github.yehortpk.router.models.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ParserProgress {
    int id;
    String title;
    int totalPages;
    List<PageProgress> pages;

    MetadataStatusEnum metadataStatus;

    int parsedVacanciesCnt;
    int newVacanciesCnt;

    @Getter
    @AllArgsConstructor
    public static class PageProgress {
        private final int id;
        @Setter
        private PageProgressStatusEnum status;
        @Setter
        private int parsedVacanciesCnt;
        private final List<PageLog> logs = new ArrayList<>();


        public record PageLog(int pageID, LogLevelEnum level, LocalDateTime timestamp, String message){}
    }

}
