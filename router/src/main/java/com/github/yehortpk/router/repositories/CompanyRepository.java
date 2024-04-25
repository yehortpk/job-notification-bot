package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.client.Client;
import com.github.yehortpk.router.models.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Client> {
    Company findByCompanyId(long companyId);
    List<Company> findByIsEnabledTrue();
}
