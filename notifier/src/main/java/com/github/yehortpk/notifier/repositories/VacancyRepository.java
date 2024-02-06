package com.github.yehortpk.notifier.repositories;

import com.github.yehortpk.notifier.models.VacancyDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepository extends JpaRepository<VacancyDAO, String> {}
