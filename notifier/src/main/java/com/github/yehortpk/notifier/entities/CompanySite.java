package com.github.yehortpk.notifier.entities;

import com.github.yehortpk.notifier.models.VacancyDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public interface CompanySite {
    Set<VacancyDTO> parseAllVacancies() throws IOException;
}
