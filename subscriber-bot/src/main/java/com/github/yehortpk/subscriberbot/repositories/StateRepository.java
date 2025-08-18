package com.github.yehortpk.subscriberbot.repositories;

import com.github.yehortpk.subscriberbot.models.UserDAO;
import org.springframework.data.repository.CrudRepository;

public interface StateRepository extends CrudRepository<UserDAO, Long> {}
