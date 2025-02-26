package com.github.yehortpk.parser.scrapper.site.metadata;

import com.github.yehortpk.parser.exceptions.RequestMetadataParsingException;
import com.github.yehortpk.parser.scrapper.page.PageScrapperResponse;

import java.util.Map;

public interface SiteMetadataParser {
    int extractPagesCount(PageScrapperResponse metadataResponse) throws RequestMetadataParsingException;
    Map<String, String> extractHeaders(PageScrapperResponse metadataResponse) throws RequestMetadataParsingException;
    Map<String, String> extractData(PageScrapperResponse metadataResponse) throws RequestMetadataParsingException;
    Map<String, String> extractCookies(PageScrapperResponse metadataResponse) throws RequestMetadataParsingException;
}
