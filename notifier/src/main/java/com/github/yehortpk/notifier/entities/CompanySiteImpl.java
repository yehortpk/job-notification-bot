package com.github.yehortpk.notifier.entities;

import com.github.yehortpk.notifier.entities.parsers.PageParser;
import com.github.yehortpk.notifier.entities.parsers.ThreadsPageParser;
import com.github.yehortpk.notifier.models.CompanyDTO;
import com.github.yehortpk.notifier.models.VacancyDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
@Setter
@Getter
@ToString
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.NO)
public abstract class CompanySiteImpl implements CompanySite{
    private ThreadsPageParser pageParser;
    private CompanyDTO company;

    @Override
    public Set<VacancyDTO> parseAllVacancies() {
        Set<VacancyDTO> vacancies = new HashSet<>();

        String pageTemplateLink = company.getJobsTemplateLink();

        String firstPageUrl = String.format(pageTemplateLink, 1);

        Document firstPage = pageParser.loadPage(firstPageUrl);
        List<Document> pages = new ArrayList<>();
        pages.add(firstPage);

        int pagesCount = getPagesCount(firstPage);
        if (pagesCount > 1) {
            List<Future<Document>> futures = new ArrayList<>();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(pagesCount - 1);

            for (int pageId = 2; pageId <= pagesCount; pageId++) {
                String pageUrl = String.format(pageTemplateLink, pageId);
                Future<Document> future = executor.submit(new PageLoader(pageParser, pageUrl));
                futures.add(future);
            }

            for (Future<Document> future : futures) {
                try {
                    Document page = future.get();
                    pages.add(page);
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Page wasn't parsed");
                }
            }

            executor.close();
        }

        for (Document page : pages) {
            List<Element> vacancyBlocks = getVacancyBlocks(page);

            for (Element vacancyBlock : vacancyBlocks) {
                VacancyDTO vacancy = getVacancyFromBlock(vacancyBlock);
                vacancy.setCompanyID(company.getCompanyId());
                vacancies.add(vacancy);
            }
        }

        return vacancies;
    }
    public abstract int getPagesCount(Document doc);
    public abstract List<Element> getVacancyBlocks(Document page);
    public abstract VacancyDTO getVacancyFromBlock(Element block);
    public abstract Map<String, String> getHeaders();
}

class PageLoader implements Callable<Document> {
    private final String pageUrl;
    private final PageParser pageParser;

    PageLoader(PageParser pageParser, String pageUrl) {
        this.pageUrl = pageUrl;
        this.pageParser = pageParser;
    }

    @Override
    public Document call() {
        return pageParser.loadPage(pageUrl);
    }
}
