package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.vacancy.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    @Query("SELECT v.vacancyId FROM Vacancy v")
    List<Long> findAllIds();
}
