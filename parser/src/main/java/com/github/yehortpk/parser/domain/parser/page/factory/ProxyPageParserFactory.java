package com.github.yehortpk.parser.domain.parser.page.factory;

import com.github.yehortpk.parser.domain.parser.page.ComponentPageParser;
import com.github.yehortpk.parser.domain.parser.page.DefaultPageParser;
import com.github.yehortpk.parser.domain.parser.page.PageParser;
import com.github.yehortpk.parser.domain.parser.page.wrapper.ProxyPoolPageParserWrapper;
import com.github.yehortpk.parser.services.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "parser", name = "mode", havingValue = "proxy")
public class ProxyPageParserFactory implements PageParserFactory {
    @Autowired
    private ProxyService proxyService;

    public PageParser createDefaultPageParser() {
        return new ProxyPoolPageParserWrapper(new DefaultPageParser(), proxyService);
    }

    public PageParser createComponentPageParser(String dynamicQueryElement) {
        return new ProxyPoolPageParserWrapper(new ComponentPageParser(dynamicQueryElement), proxyService);
    }
}
