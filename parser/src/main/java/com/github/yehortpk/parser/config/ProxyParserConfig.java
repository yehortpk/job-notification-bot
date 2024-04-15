package com.github.yehortpk.parser.config;

import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.domain.connectors.ProxyPageConnector;
import com.github.yehortpk.parser.domain.scrappers.ComponentPageScrapper;
import com.github.yehortpk.parser.domain.scrappers.DefaultPageScrapper;
import com.github.yehortpk.parser.services.ProxyService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "parser", name = "mode", havingValue = "proxy")
public class ProxyParserConfig {
    @Bean
    public PageConnector defaultPageScrapperLoader(DefaultPageScrapper defaultPageScrapper, ProxyService proxyService) {
        return new ProxyPageConnector(defaultPageScrapper, proxyService);
    }

    @Bean
    public PageConnector componentPageScrapperLoader(ComponentPageScrapper defaultPageScrapper, ProxyService proxyService) {
        return new ProxyPageConnector(defaultPageScrapper, proxyService);
    }
}
