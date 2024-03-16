package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.client.Client;
import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.filter.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilterRepository extends JpaRepository<Filter, Long> {
    List<Filter> findByCompanyAndClient(Company company, Client client);
    @Query(value = "select * from filter where company_id=:companyId and client_id=:clientId", nativeQuery = true)
    List<Filter> findByCompanyIdAndClientId(long companyId, long clientId);
}
