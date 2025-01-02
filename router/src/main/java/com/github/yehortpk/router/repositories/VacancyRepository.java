package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.vacancy.Vacancy;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    @Query("SELECT v.vacancyId FROM Vacancy v")
    List<Long> findAllIds();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"company"})
    @NonNull
    List<Vacancy> findAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM Vacancy v WHERE v.link IN :links")
    void deleteByLinkIn(List<String> links);

    Page<Vacancy> findByTitleContaining(String title, Pageable pageable);
}
