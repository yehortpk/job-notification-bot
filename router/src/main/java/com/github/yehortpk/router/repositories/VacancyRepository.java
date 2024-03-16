package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.vacancy.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
}
