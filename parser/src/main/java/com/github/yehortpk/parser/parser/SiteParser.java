package com.github.yehortpk.parser.parser;

import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.PageDTO;
import com.github.yehortpk.parser.models.VacancyDTO;
import com.github.yehortpk.parser.crawler.*;

import java.util.List;

/**
 * Interface for all page parsers. Require method parseVacancies
 * for specific {@link CompanyDTO}
 * @see APISiteCrawler
 * @see StaticSiteCrawler
 * @see DynamicSiteCrawler
 */
public interface SiteParser {
    /**
     * Parse all vacancies from {@link PageDTO} page
     *
     * @return list of vacancies
     */
    List<VacancyDTO> parseVacancies(PageDTO page) throws Exception;
}
