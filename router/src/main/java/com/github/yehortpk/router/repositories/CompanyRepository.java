package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.CompanyDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyDAO, Long> {
    @Query("select c from CompanyDAO c where c.isEnabled=true")
    List<CompanyDAO> findByIsEnabledTrue();
}
