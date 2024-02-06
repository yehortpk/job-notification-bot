package com.github.yehortpk.notifier.entities;

import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public interface CompanySiteInterface {
    void setCompany(CompanyDTO companyDTO);
    Set<VacancyDTO> parseAllVacancies() throws IOException;
}
