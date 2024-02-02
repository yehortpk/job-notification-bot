package com.github.yehortpk.notifier.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash("vacancy")
@Builder
public class VacancyDAO {
    @Id
    private int companyID;
    private int vacancyId;
    private String link;
    private String title;
    private int minSalary;
    private int maxSalary;
}
