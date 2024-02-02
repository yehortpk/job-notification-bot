package com.github.yehortpk.notifier.services;

import com.github.yehortpk.notifier.models.VacancyDAO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.yehortpk.notifier.repositories.VacancyRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VacancyService {
    @Autowired
    private VacancyRepository vacancyRepository;

    public Set<VacancyDAO> getPersistedVacancies() {
        return vacancyRepository.findAll();
    }

    public Set<VacancyDTO> getDifference(Set<VacancyDTO> allVacancies, Set<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> result = new HashSet<>(allVacancies);
        result.removeAll(persistedVacancies);

        return result;
    }

    public Set<VacancyDTO> getOutdatedVacancies(Set<VacancyDTO> allVacancies, Set<VacancyDTO> persistedVacancies) {
        Set<VacancyDTO> result = new HashSet<>(persistedVacancies);
        result.removeAll(allVacancies);

        return result;
    }

    public void removeVacancies(Set<VacancyDTO> outdatedVacancies) {
        Set<VacancyDAO> vacancies = outdatedVacancies.stream().map(VacancyDTO::toDAO).collect(Collectors.toSet());
        vacancyRepository.deleteAll(vacancies);
    }

    public void addVacancies(Set<VacancyDTO> newVacancies) {
        Set<VacancyDAO> vacancies = newVacancies.stream().map(VacancyDTO::toDAO).collect(Collectors.toSet());
        vacancyRepository.saveAll(vacancies);
    }
}
