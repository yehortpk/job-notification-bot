package com.github.yehortpk.parser.services;

import com.github.yehortpk.parser.models.CompanyDTO;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatusBarService {
    private final Map<Integer, ProgressBar> companiesBars = new HashMap<>();

    public void addCompany(CompanyDTO company, int pagesCount) {
        ProgressBar pb = new ProgressBar(company.getTitle(), pagesCount);
        companiesBars.put(company.getCompanyId(), pb);
    }

    public void checkParsedPage(int companyId) {
        companiesBars.get(companyId).step();
    }

    public void closeBar() {
        companiesBars.forEach((integer, progressBar) -> progressBar.close());
    }
}
