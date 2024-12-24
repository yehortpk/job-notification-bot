package com.github.yehortpk.router.controllers;

import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.vacancy.VacanciesPageDTO;
import com.github.yehortpk.router.models.vacancy.VacancyCompanyDTO;
import com.github.yehortpk.router.services.ClientService;
import com.github.yehortpk.router.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Controller responsible for handling filter related requests
 */
@RestController
@RequestMapping("/filter")
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
                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        if (pageId <= 0) {
            throw new IllegalArgumentException("page parameter could not be less then 1");
        }

        List<VacancyCompanyDTO> filteredVacancies = filterService.getVacanciesByFilter(filterId).stream()
                .map(vac -> modelMapper.map(vac, VacancyCompanyDTO.class)).toList();

        int totalSize = filteredVacancies.size();
        int totalPages = Math.max((int) Math.ceil((double)totalSize / pageSize), 1);

        if (pageId > totalPages) {
            throw new IllegalArgumentException("page parameter could not be greater then total pages count");
        }

        int start = Math.max(0, pageId - 1) * pageSize;
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
