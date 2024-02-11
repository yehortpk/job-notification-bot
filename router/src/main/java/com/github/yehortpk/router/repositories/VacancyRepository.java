package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.VacancyDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepository extends JpaRepository<VacancyDAO, Long> {
}
