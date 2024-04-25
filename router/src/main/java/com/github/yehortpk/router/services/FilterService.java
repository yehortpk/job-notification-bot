package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.repositories.FilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * This service provides methods for work with {@link FilterRepository}
 */
@Component
@RequiredArgsConstructor
public class FilterService {
    private final FilterRepository filterRepository;

    public List<Filter> findByCompanyIdAndClientId(long companyId, long clientId) {
        return filterRepository.findByCompanyIdAndClientId(companyId, clientId);
    }

    public Optional<Filter> findById(long filterId) {
        return filterRepository.findById(filterId);
    }

    public void deleteFilter(long filterId) {
        filterRepository.deleteById(filterId);
    }

    /**
     * Insert filter into database without create an entity
     * @param companyId id of company
     * @param clientId id of client
     * @param filter filter as a string
     */
    public void addRawFilter(long companyId, long clientId, String filter) {
        filterRepository.saveByRawIds(companyId, clientId, filter);
    }
}
