package com.github.yehortpk.parser.domain.parser.page.factory;

import com.github.yehortpk.parser.domain.parser.page.PageParser;

public interface PageParserFactory {
    PageParser createDefaultPageParser();
    PageParser createComponentPageParser(String dynamicQuerySelector);
}
