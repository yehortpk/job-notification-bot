package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.ClientDAO;
import com.github.yehortpk.router.models.CompanyDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyDAO, Long>, JpaSpecificationExecutor<ClientDAO> {
    CompanyDAO findByCompanyId(long companyId);
}
