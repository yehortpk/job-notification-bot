package com.github.yehortpk.notifier.entities;

import com.github.yehortpk.notifier.entities.parsers.PageParser;
import com.github.yehortpk.notifier.entities.parsers.ThreadsPageParser;
import com.github.yehortpk.notifier.models.VacancyDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Component
public abstract class MultiplePageCompanySite implements CompanySiteInterface{
    PageParser pageParser = new ThreadsPageParser();

    @Override
    public Set<VacancyDTO> parseAllVacancies() {
        Set<VacancyDTO> vacancies = new HashSet<>();

        String pageTemplateLink = getPageTemplateLink();
        String firstPageUrl = String.format(pageTemplateLink, 1);

        System.out.println("Try: " + firstPageUrl);
        Document firstPage = pageParser.loadPage(firstPageUrl);
        System.out.println("Page parsed: " + firstPageUrl);
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

            executor.shutdown();
        }

        for (Document page : pages) {
            List<Element> vacancyBlocks = getVacancyBlocks(page);

            for (Element vacancyBlock : vacancyBlocks) {
                VacancyDTO vacancy = getVacancyFromBlock(vacancyBlock);
                vacancies.add(vacancy);
            }
        }

        return vacancies;
    }

    public abstract String getPageTemplateLink();
    public abstract int getPagesCount(Document doc);
    public abstract List<Element> getVacancyBlocks(Document page);
    public abstract VacancyDTO getVacancyFromBlock(Element block);
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
        System.out.println("Try: " + pageUrl);
        Document page = pageParser.loadPage(pageUrl);
        System.out.println("Page parsed " + pageUrl);

        return page;
    }
}
