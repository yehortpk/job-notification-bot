package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.filter.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilterRepository extends JpaRepository<Filter, Long> {
    @Query(value = "select * from filter where client_id=:clientId", nativeQuery = true)
    List<Filter> findByClientId(long clientId);

    @Modifying
    @Query(value = "INSERT INTO filter (client_id, filter) VALUES (:clientId, :filter)", nativeQuery = true)
    void saveByRawIds(Long clientId, String filter);
}
