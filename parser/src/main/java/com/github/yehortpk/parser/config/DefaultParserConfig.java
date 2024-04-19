package com.github.yehortpk.parser.config;

import com.github.yehortpk.parser.domain.connectors.DelayedPageConnector;
import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.domain.scrappers.ComponentPageScrapper;
import com.github.yehortpk.parser.domain.scrappers.DefaultPageScrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config for delayed parsing strategy when parser.mode property has value default. Uses {@link DelayedPageConnector} as
 * a default connector.
 */
@Configuration
@ConditionalOnProperty(prefix = "parser", name = "mode", havingValue = "default")
public class DefaultParserConfig {
    @Bean
    public PageConnector defaultPageConnector(DefaultPageScrapper defaultPageScrapper) {
        return new DelayedPageConnector(defaultPageScrapper);
    }

    @Bean
    public PageConnector componentPageConnector(ComponentPageScrapper defaultPageScrapper) {
        return new DelayedPageConnector(defaultPageScrapper);
    }
}
