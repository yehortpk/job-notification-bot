package com.github.yehortpk.parser.domain.parser.page;

import com.github.yehortpk.parser.domain.parser.page.wrapper.ProxyPoolPageParserWrapper;
import com.github.yehortpk.parser.services.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PageParserFactory {
    @Autowired
    private ApplicationContext ctx;

    @Value("${parser.mode:default}")
    private String parserMode;

    public PageParser createDefaultPageParser() {
        if (Objects.equals(parserMode, "default")) {
            return new DefaultPageParser();
        } else if (Objects.equals(parserMode, "proxy")) {
            ProxyService proxyService = ctx.getBean(ProxyService.class);
            return new ProxyPoolPageParserWrapper(new DefaultPageParser(), proxyService);
        }

        throw new RuntimeException("parser.mode have to be empty or contain value default or proxy");
    }

    public PageParser createComponentPageParser(String dynamicQueryElement) {
        if (Objects.equals(parserMode, "default")) {
            return new ComponentPageParser(dynamicQueryElement);
        } else if (Objects.equals(parserMode, "proxy")) {
            ProxyService proxyService = ctx.getBean(ProxyService.class);
            return new ProxyPoolPageParserWrapper(new ComponentPageParser(dynamicQueryElement), proxyService);
        }

        throw new RuntimeException("parser.mode have to be empty or contain value default or proxy");
    }
}
