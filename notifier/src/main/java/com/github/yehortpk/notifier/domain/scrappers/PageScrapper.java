package com.github.yehortpk.notifier.domain.scrappers;

import com.github.yehortpk.notifier.models.PageConnectionParams;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface PageScrapper {
    String scrapPage(PageConnectionParams pageConnectionParams) throws IOException;
}
