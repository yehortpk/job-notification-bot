package com.github.yehortpk.notifier.domain.parsers.site;

import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;

import java.util.Set;

public interface SiteParser {
    Set<VacancyDTO> parseAllVacancies();
    void setCompany(CompanyDTO company);
}
