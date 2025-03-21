package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.repositories.FilterRepository;
import com.github.yehortpk.router.utils.FilterParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This service provides methods for work with {@link FilterRepository}
 */
@Component
@RequiredArgsConstructor
public class FilterService {
    private final FilterRepository filterRepository;
    private final VacancyService vacancyService;

    public Optional<Filter> findById(long filterId) {
        return filterRepository.findById(filterId);
    }

    public void deleteFilter(long filterId) {
        filterRepository.deleteById(filterId);
    }

    /**
     * Insert filter into database without create an entity
     * @param clientId id of client
     * @param filter filter as a string
     */
    public void addRawFilter(long clientId, String filter) {
        filterRepository.saveByRawIds(clientId, filter);
    }

    /**
     * Returns vacancies that pass the filter
     * @param filterId id of filter in database
     * @return list of filtered vacancies
     */
    public List<Vacancy> getVacanciesByFilter(long filterId) {
        Filter filter = findById(filterId).orElseThrow();
        List<Vacancy> allVacancies = vacancyService.getAllVacancies();
        FilterParser filterParser = new FilterParser(filter.getFilter());

        List<Vacancy> result = new ArrayList<>();
        for (Vacancy vacancy : allVacancies) {
            if (filterParser.isStringApplicable(vacancy.getCompany().getTitle() + " " + vacancy.getTitle())) {
                result.add(vacancy);
            }
        }

        return result;
    }

    @Transactional
    public void addFilter(FilterDTO filter) {
        addRawFilter(filter.getClientId(), filter.getFilter());
    }

    public List<Filter> getAllFilters() {
        return filterRepository.findAll();
    }
}
