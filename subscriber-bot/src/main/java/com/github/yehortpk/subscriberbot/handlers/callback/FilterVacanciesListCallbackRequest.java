package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.dtos.CompanyShortInfoDTO;
import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import com.github.yehortpk.subscriberbot.dtos.VacancyShortDTO;
import com.github.yehortpk.subscriberbot.dtos.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilterVacanciesListCallbackRequest extends CallbackDataRequestHandlerImpl{
    private final FilterService filterService;
    private final StateService stateService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();
        String[] filterDetails = callbackData
                .split("vacancies_list=")[1].split(":");

        int filterId = Integer.parseInt(filterDetails[0]);

        String text;
        List<VacancyShortDTO> vacanciesByFilter = filterService.getVacanciesByFilter(filterId);
        if (!vacanciesByFilter.isEmpty()) {
            Map<Long, String> companiesMap = new HashMap<>();
            for (CompanyShortInfoDTO company : filterService.getCompaniesList()) {
                companiesMap.computeIfAbsent(company.getCompanyId(), k -> company.getCompanyTitle());
            }

            Map<Integer, List<VacancyShortDTO>> vacanciesByCompany = vacanciesByFilter.stream()
                    .collect(Collectors.groupingBy(VacancyShortDTO::getCompanyId));

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(String.format("There are %s vacancies by this filter:\n", vacanciesByFilter.size()));
            vacanciesByCompany.forEach((companyId, vacancies) -> {
                String companyTitle = companiesMap.get(Long.valueOf(companyId));
                stringBuilder.append(String.format("\nVacancies for <b>%s</b> company\n", companyTitle));
                for (int i = 0; i < vacancies.size(); i++) {
                    VacancyShortDTO vacancy = vacancies.get(i);
                    stringBuilder.append(String.format("%s. <a href='%s'>%s</a>\n", i + 1, vacancy.getVacancyURL(),
                            vacancy.getVacancyTitle()));
                }
            });

            text = stringBuilder.toString();
        } else {
            text = "There is no vacancies by this filter";
        }

        UserDTO user = userRequest.getUser();
        user.setUserState(UserState.FILTER_VACANCIES_LIST);
        user.addRequestCallbackData(callbackData);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(text)
                .replyMarkup(BackInlineMarkup.getMarkup("filter-vacancies"))
                .build();
    }

    @Override
    public UserState getExpectedState() {
        return UserState.FILTER_INFO_STATE;
    }

    @Override
    public String getExpectedData() {
        return "vacancies_list";
    }
}
