package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.company.Company;
import com.github.yehortpk.router.models.company.CompanyDTO;
import com.github.yehortpk.router.models.filter.Filter;
import com.github.yehortpk.router.models.filter.FilterDTO;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import com.github.yehortpk.router.models.vacancy.VacancyDTO;
import com.github.yehortpk.router.models.vacancy.VacancyNotificationDTO;
import com.github.yehortpk.router.repositories.CompanyRepository;
import com.github.yehortpk.router.repositories.FilterRepository;
import com.github.yehortpk.router.repositories.VacancyRepository;
import com.github.yehortpk.router.utils.FilterParser;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotifierService {
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    FilterRepository filterRepository;

    @Autowired
    VacancyRepository vacancyRepository;

    @Autowired
    KafkaTemplate<String, VacancyNotificationDTO> kafkaTemplate;

    public void addVacancies(List<VacancyDTO> vacancies) {
        List<Vacancy> vacancyEntities = new ArrayList<>();
        for (VacancyDTO vacancy : vacancies) {
            vacancyEntities.add(
                    Vacancy.builder()
                            .company(companyRepository.getReferenceById((long) vacancy.getCompanyID()))
                            .vacancyId(vacancy.getVacancyID())
                            .title(vacancy.getTitle())
                            .minSalary(vacancy.getMinSalary())
                            .maxSalary(vacancy.getMaxSalary())
                            .link(vacancy.getLink())
                            .build()
            );
        }
        vacancyRepository.saveAll(vacancyEntities);
    }

    @Transactional
    public void notifyUsers(VacancyDTO vacancy) {
        Company company = companyRepository.findByCompanyId(vacancy.getCompanyID());
        company.getSubscribers().forEach((subscriber) -> {
            boolean applicable = true;
            List<Filter> filters = filterRepository.findByCompanyAndClient(company, subscriber);

            if (!filters.isEmpty()) {
                for (Filter filter : filters) {
                    if (isApplicable(vacancy.getTitle(), filter.getFilter())) {
                        applicable = true;
                        break;
                    } else {
                        applicable = false;
                    }
                }
            }

            System.out.println("Applicable: " + applicable);
            if (applicable) {

                VacancyNotificationDTO vacancyNotification = VacancyNotificationDTO.builder()
                        .chatId(subscriber.getChatId())
                        .vacancyTitle(vacancy.getTitle())
                        .companyTitle(company.getTitle())
                        .maxSalary(vacancy.getMaxSalary())
                        .minSalary(vacancy.getMinSalary())
                        .link(vacancy.getLink())
                        .build();

                kafkaTemplate.sendDefault(vacancyNotification);
            }
        });
    }

    public boolean isApplicable(String input, String filter) {
        FilterParser filterParser = new FilterParser(filter);

        List<String[]> binaryMatches = filterParser.getBinaryMatches();
        List<String> negativeMatches = filterParser.getNegativeMatches();
        List<String> mandatoryMatches = filterParser.getMandatoryMatches();

        input = input.toLowerCase();

        for (String negativeMatch : negativeMatches) {
            if (input.contains(negativeMatch)) {
                return false;
            }
        }

        for (String defaultMatch : mandatoryMatches) {
            if (!input.contains(defaultMatch)) {
                return false;
            }
        }

        boolean result = true;
        for (String[] binaryMatch : binaryMatches) {
            for (String match : binaryMatch) {
                if (input.contains(match)) {
                    return true;
                }
            }
            result = false;
        }

        return result;
    }

}
