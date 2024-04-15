package com.github.yehortpk.parser.domain.parsers.site;

import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.VacancyDTO;

import java.util.Set;

public interface SiteParser {
    Set<VacancyDTO> parseAllVacancies();
    void setCompany(CompanyDTO company);
}
