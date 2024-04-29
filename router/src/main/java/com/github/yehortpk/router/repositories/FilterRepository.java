package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.filter.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilterRepository extends JpaRepository<Filter, Long> {
    @Query(value = "select * from filter where company_id=:companyId and client_id=:clientId", nativeQuery = true)
    List<Filter> findByCompanyIdAndClientId(long companyId, long clientId);

    @Modifying  // Required for update queries
    @Query(value = "INSERT INTO filter (company_id, client_id, filter) VALUES (:companyId, :clientId, :filter)", nativeQuery = true)
    void saveByRawIds(Long companyId, Long clientId, String filter);
}
