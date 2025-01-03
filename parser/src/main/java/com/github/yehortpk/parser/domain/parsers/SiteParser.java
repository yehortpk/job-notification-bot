package com.github.yehortpk.parser.domain.parsers;

import com.github.yehortpk.parser.domain.connectors.PageConnector;
import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.VacancyDTO;

import java.util.Set;

/**
 * Interface for all parsers. Uses {@link PageConnector} to acquire page connection. Require method parseAllVacancies
 * for specific {@link CompanyDTO}
 * @see SiteParserImpl
 * @see APISiteParser
 * @see StaticSiteParser
 * @see ComponentSiteParser
 */
public interface SiteParser {
    /**
     * Parse all vacancies for specific {@link CompanyDTO}
     * @return set of vacancies
     */
    Set<VacancyDTO> parseVacancies(CompanyDTO company);
}
