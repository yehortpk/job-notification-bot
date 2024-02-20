package com.github.yehortpk.notifier.entities.companies;

import com.github.yehortpk.notifier.models.VacancyDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public interface CompanySite {
    Set<VacancyDTO> parseAllVacancies();

    default Map<String, String> createHeaders() {
        return null;
    }
}
