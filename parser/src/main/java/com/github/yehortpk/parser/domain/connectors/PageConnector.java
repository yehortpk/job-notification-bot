package com.github.yehortpk.parser.domain.connectors;

import com.github.yehortpk.parser.models.PageConnectionParams;

import java.io.IOException;

public interface PageConnector {
    String connectToPage(PageConnectionParams pageConnectionParams) throws IOException;
}
