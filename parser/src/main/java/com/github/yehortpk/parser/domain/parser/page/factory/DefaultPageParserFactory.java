package com.github.yehortpk.parser.domain.parser.page.factory;

import com.github.yehortpk.parser.domain.parser.page.ComponentPageParser;
import com.github.yehortpk.parser.domain.parser.page.DefaultPageParser;
import com.github.yehortpk.parser.domain.parser.page.PageParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "parser", name = "mode", matchIfMissing = true, havingValue = "default")
public class DefaultPageParserFactory implements PageParserFactory {

    public PageParser createDefaultPageParser() {
        return new DefaultPageParser();
    }

    public PageParser createComponentPageParser(String dynamicQueryElement) {
        return new ComponentPageParser(dynamicQueryElement);
    }
}
