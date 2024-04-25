package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {}
