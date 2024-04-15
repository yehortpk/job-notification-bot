package com.github.yehortpk.parser.domain.scrappers;

import com.github.yehortpk.parser.models.PageConnectionParams;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface PageScrapper {
    String scrapPage(PageConnectionParams pageConnectionParams) throws IOException;
}
