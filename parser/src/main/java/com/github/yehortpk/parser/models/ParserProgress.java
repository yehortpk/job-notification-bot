package com.github.yehortpk.parser.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParserProgress {
    private int id;
    private String title;
    private int totalPages;
    private List<PageProgress> pages = new ArrayList<>();

    private MetadataStatusEnum metadataStatus = MetadataStatusEnum.PENDING;

    private int parsedVacanciesCnt = 0;
    private int newVacanciesCnt = 0;
    private int outdatedVacanciesCnt = 0;

    public ParserProgress(int id, String title) {
        this.id = id;
        this.title = title;
        this.totalPages = 1;
        this.initPages(totalPages);
    }

    public void initPages(int totalPages) {
        this.pages = new ArrayList<>();
        this.totalPages = totalPages;
        for (int i = 0; i < totalPages; i++) {
            pages.add(new PageProgress(i + 1));
        }
    }

    public void markPageDone(int page) {
        pages.get(page - 1).setStatus(PageProgressStatusEnum.STEP_DONE);
    }

    public void markPageError(int page) {
        pages.get(page - 1).setStatus(PageProgressStatusEnum.STEP_ERROR);
    }

    public void addPageLog(int page, LogLevelEnum level, String message) {
        pages.get(page - 1).addLog(new PageProgress.PageLog(page, level, LocalDateTime.now(), message));
    }

    public void setPageParsedVacanciesCount(int page, int count) {
        pages.get(page - 1).setParsedVacanciesCnt(count);
    }

    @Getter
    public static class PageProgress {
        private final int id;
        @Setter
        private PageProgressStatusEnum status = PageProgressStatusEnum.STEP_PENDING;
        @Setter
        private int parsedVacanciesCnt = 0;
        private final List<PageLog> logs = new ArrayList<>();

        public PageProgress(int id) {
            this.id = id;
        }

        public void addLog(PageLog log) {
            this.logs.add(log);
        }

        public record PageLog(int pageID, LogLevelEnum level, LocalDateTime timestamp, String message){}
    }

    public enum LogLevelEnum {
        ERROR,
        INFO
    }
}
