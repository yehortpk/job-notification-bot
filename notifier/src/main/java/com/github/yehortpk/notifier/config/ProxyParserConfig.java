package com.github.yehortpk.notifier.config;

import com.github.yehortpk.notifier.domain.connectors.PageConnector;
import com.github.yehortpk.notifier.domain.connectors.ProxyPageConnector;
import com.github.yehortpk.notifier.domain.scrappers.ComponentPageScrapper;
import com.github.yehortpk.notifier.domain.scrappers.DefaultPageScrapper;
import com.github.yehortpk.notifier.services.ProxyService;
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
