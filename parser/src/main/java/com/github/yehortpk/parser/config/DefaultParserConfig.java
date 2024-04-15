package com.github.yehortpk.parser.config;

import com.github.yehortpk.parser.domain.connectors.DelayedPageConnector;
import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.domain.scrappers.ComponentPageScrapper;
import com.github.yehortpk.parser.domain.scrappers.DefaultPageScrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "parser", name = "mode", havingValue = "default")
public class DefaultParserConfig {
    @Bean
    public PageConnector defaultPageScrapperLoader(DefaultPageScrapper defaultPageScrapper) {
        return new DelayedPageConnector(defaultPageScrapper);
    }

    @Bean
    public PageConnector componentPageScrapperLoader(ComponentPageScrapper defaultPageScrapper) {
        return new DelayedPageConnector(defaultPageScrapper);
    }
}
