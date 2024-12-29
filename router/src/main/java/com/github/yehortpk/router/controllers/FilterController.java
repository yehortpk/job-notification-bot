package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.vacancy.VacanciesPageDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyCompanyDTO;
import com.github.yehortpk.router.services.ClientService;
import com.github.yehortpk.router.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.beanutils.BeanComparator;

import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Controller responsible for handling filter related requests
 */
@RestController
@RequestMapping("/filter")

@CrossOrigin("http://localhost:4200")
@RequiredArgsConstructor
public class FilterController {
    private final ModelMapper modelMapper;
    private final FilterService filterService;
    private final ClientService clientService;

    @GetMapping("/chat/{chat_id}")
    public List<FilterDTO> getFilters(
            @PathVariable("chat_id") int chatId
    ) {
        return clientService.findById(chatId).getFilters()
                .stream().map((filter) -> modelMapper.map(filter, FilterDTO.class)).toList();
    }

    @GetMapping
    public List<FilterDTO> getAllFilters() {
        return filterService.getAllFilters()
                .stream().map((filter) -> modelMapper.map(filter, FilterDTO.class)).toList();
    }

    @GetMapping("/{filter_id}")
    public FilterDTO getFilter(@PathVariable("filter_id") long filterId) {
        Filter filter = filterService.findById(filterId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "Filter not found"));
        return modelMapper.map(filter, FilterDTO.class);
    }

    @PostMapping
    public void addFilter(@RequestBody FilterDTO filter) {
        filterService.addFilter(filter);
    }

    @DeleteMapping("/{filter_id}")
    public void deleteFilter(@PathVariable("filter_id") long filterId) {
        filterService.deleteFilter(filterId);
    }

    @GetMapping("/{filter_id}/vacancies")
    public VacanciesPageDTO getVacanciesByFilter(@PathVariable("filter_id") long filterId,
                                                 @RequestParam(value = "page") int pageId,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                 @RequestParam(value = "sortBy", defaultValue = "parsedAt") String sortBy,
                                                 @RequestParam(value = "sortDir", defaultValue = "DESC") String sortDir) {
        // Check if field exists in Vacancy entity
        if (!PropertyUtils.isReadable(new Vacancy(), sortBy)) {
            throw new IllegalArgumentException(String.format("Property %s doesn't exist in the sorted entity", sortBy));
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.valueOf(sortDir);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Use only ASC/DESC for sortDir property");
        }

        if (pageId < 0) {
            throw new IllegalArgumentException("page parameter could not be less then 0");
        }

        Comparator<Vacancy> comparator = new BeanComparator<>(sortBy);
        if (direction.isDescending()) {
            comparator = comparator.reversed();
        }

        List<VacancyCompanyDTO> filteredVacancies = filterService.getVacanciesByFilter(filterId).stream()
                .sorted(comparator)
                .map(vac -> modelMapper.map(vac, VacancyCompanyDTO.class)).toList();

        int totalSize = filteredVacancies.size();
        int totalPages = Math.max((int) Math.ceil((double)totalSize / pageSize), 1);

        if (pageId > totalPages) {
            throw new IllegalArgumentException("page parameter could not be greater then total pages count");
        }

        int start = pageId * pageSize;
        int end = start + Math.min(totalSize - start, pageSize);

        return VacanciesPageDTO.builder()
                .vacancies(filteredVacancies.subList(start, end))
                .currentPage(pageId)
                .totalVacancies(totalSize)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }
}
