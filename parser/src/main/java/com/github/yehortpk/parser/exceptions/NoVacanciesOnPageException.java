package com.github.yehortpk.parser.exceptions;

import lombok.Getter;
import lombok.experimental.StandardException;

@Getter
@StandardException
public class NoVacanciesOnPageException extends Exception {
    private int pageId;

    public NoVacanciesOnPageException(int pageId) {
        super(String.format("No vacancies on page %s", pageId));
        this.pageId = pageId;
    }
}
