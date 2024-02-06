package com.github.yehortpk.notifier.repositories;

import com.github.yehortpk.notifier.models.CompanyDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepository extends JpaRepository<CompanyDAO, Long> {
    @Query("select c from CompanyDAO c where c.isEnabled=true")
    List<CompanyDAO> findByIsEnabledTrue();
}
