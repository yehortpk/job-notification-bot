package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.ClientDAO;
import com.github.yehortpk.router.models.CompanyDAO;
import com.github.yehortpk.router.models.FilterDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilterRepository extends JpaRepository<FilterDAO, Long> {
    List<FilterDAO> findByCompanyAndClient(CompanyDAO companyDAO, ClientDAO clientDAO);
    @Query(value = "select * from filter where company_id=:companyId and client_id=:clientId", nativeQuery = true)
    List<FilterDAO> findByCompanyIdAndClientId(long companyId, long clientId);
}
