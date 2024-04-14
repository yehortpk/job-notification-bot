package com.github.yehortpk.notifier.domain.connectors;

import com.github.yehortpk.notifier.models.PageConnectionParams;

import java.io.IOException;

public interface PageConnector {
    String connectToPage(PageConnectionParams pageConnectionParams) throws IOException;
}
