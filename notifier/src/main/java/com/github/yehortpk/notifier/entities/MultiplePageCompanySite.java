package com.github.yehortpk.notifier.entities;

import com.github.yehortpk.notifier.models.VacancyDTO;
import com.github.yehortpk.notifier.services.ProxyService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Component
public abstract class MultiplePageCompanySite implements CompanySiteInterface{
    @Autowired
    ProxyService proxyService;

    @Override
    public Set<VacancyDTO> parseAllVacancies() {
        Set<VacancyDTO> vacancies = new HashSet<>();

        String pageTemplateLink = getPageTemplateLink();
        String firstPageUrl = String.format(pageTemplateLink, 1);

        Document firstPage = loadPage(firstPageUrl);
        System.out.println("Page parsed " + firstPageUrl);
        List<Document> pages = new ArrayList<>();
        pages.add(firstPage);
        for (int pageId = 2; pageId <= getPagesCount(firstPage); pageId++) {
            String pageUrl = String.format(pageTemplateLink, pageId);

            Document page = loadPage(pageUrl);
            pages.add(page);
            System.out.println("Page parsed " + pageUrl);
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

    private Document loadPage(String pageURL) {
        System.out.println("Try " + pageURL);
        int nThreads = 30;
        List<Future<Document>> futures = new ArrayList<>();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        CompletionService<Document> completionService = new ExecutorCompletionService<>(executor);

        for (int i = 0; i < nThreads; i++) {
            Callable<Document> task = parsePage(pageURL);
            Future<Document> future = completionService.submit(task);
            futures.add(future);
        }

        int pollTimeout = 30;
        for (int i = 0; i < futures.size(); i++) {
            try {
                Future<Document> completedFuture = completionService.poll(pollTimeout, TimeUnit.SECONDS);

                if (completedFuture != null) {
                    Document result;
                    try {
                        result = completedFuture.get();
                    } catch (ExecutionException e) {
                        continue;
                    }

                    futures.forEach((fut) -> fut.cancel(true));

                    executor.shutdown();
                    return result;

                }
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
        }

        executor.shutdown();
        return loadPage(pageURL);
    }

    private Callable<Document> parsePage(String pageUrl) {
        Proxy randomProxy = proxyService.getRandomProxy();
        return () -> Jsoup.connect(pageUrl)
                .proxy(randomProxy)
                .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316" +
                        " Firefox/3.6.2")
                .header("Content-Language", "en-US")
                .get();
    }

    public abstract String getPageTemplateLink();
    public abstract int getPagesCount(Document doc);
    public abstract List<Element> getVacancyBlocks(Document page);
    public abstract VacancyDTO getVacancyFromBlock(Element block);
}
