package com.github.yehortpk.parser.scrapper.site;

import com.github.yehortpk.parser.models.CompanyDTO;
import com.github.yehortpk.parser.models.PageDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SiteScrapper {
    List<CompletableFuture<PageDTO>> scrapCompanyVacancies(CompanyDTO company);
}
