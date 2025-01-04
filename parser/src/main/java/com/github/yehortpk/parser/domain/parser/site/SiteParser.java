package com.github.yehortpk.parser.domain.parser.site;

import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.VacancyDTO;

import java.util.Set;

/**
 * Interface for all page parsers. Require method parseVacancies
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
