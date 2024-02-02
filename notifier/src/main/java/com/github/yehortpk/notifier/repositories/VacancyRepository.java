package com.github.yehortpk.notifier.repositories;

import com.github.yehortpk.notifier.models.VacancyDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface VacancyRepository extends CrudRepository<VacancyDAO, String> {
    Set<VacancyDAO> findAll();
}
