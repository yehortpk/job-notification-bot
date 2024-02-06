package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.ClientDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientDAO, Long> {}
