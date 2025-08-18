package com.github.yehortpk.subscriberbot.handlers.callback;

import com.github.yehortpk.subscriberbot.models.*;
import com.github.yehortpk.subscriberbot.models.enums.UserState;
import com.github.yehortpk.subscriberbot.markups.BackInlineMarkup;
import com.github.yehortpk.subscriberbot.markups.VacanciesListPageableMarkup;
import com.github.yehortpk.subscriberbot.services.CompanyService;
import com.github.yehortpk.subscriberbot.services.StateService;
import com.github.yehortpk.subscriberbot.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilterVacanciesListCallbackRequestHandler extends CallbackDataRequestHandlerImpl{
    private final FilterService filterService;
    private final StateService stateService;
    private final CompanyService companyService;

    @Override
    public SendMessage handleRequest(UserRequestDTO userRequest) {
        String callbackData = userRequest.getUpdate().getCallbackQuery().getData();
        String[] filterDetails = callbackData
                .split("vacancies_list=")[1].split(":");

        int filterId = Integer.parseInt(filterDetails[0]);

        return generateMessage(filterId, 1, userRequest, callbackData);

    }

    public SendMessage generateMessage(int filterId, int pageId, UserRequestDTO userRequest, String callbackData){
        String text;
        InlineKeyboardMarkup markup;
        VacanciesPageDTO vacanciesByFilter = filterService.getVacanciesByFilter(filterId, pageId);

        if (!vacanciesByFilter.getVacancies().isEmpty()) {
            Map<Long, String> companiesMap = new HashMap<>();
            for (CompanyShortInfoDTO company : companyService.getCompaniesList()) {
                companiesMap.computeIfAbsent(company.getCompanyId(), k -> company.getCompanyTitle());
            }

            Map<Long, List<VacancyCompanyDTO>> vacanciesByCompany = vacanciesByFilter.getVacancies().stream()
                    .collect(Collectors.groupingBy(vac -> vac.getCompany().getCompanyId()));

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(String.format("There are %s vacancies by this filter:\n", vacanciesByFilter.getTotalVacancies()));
            vacanciesByCompany.forEach((companyId, vacancies) -> {
                String companyTitle = companiesMap.get(companyId);
                stringBuilder.append(String.format("\nVacancies for <b>%s</b> company\n", companyTitle));
                for (int i = 0; i < vacancies.size(); i++) {
                    VacancyCompanyDTO vacancy = vacancies.get(i);
                    stringBuilder.append(String.format("%s. <a href='%s'>%s</a>\n", i + 1, vacancy.getURL(),
                            vacancy.getTitle()));
                }
            });

            int start = vacanciesByFilter.getCurrentPage() - 1;
            stringBuilder.append(String.format("\nShowing %s-%s of %s vacancies for this filter\n",
                    start * vacanciesByFilter.getPageSize() + 1,
                    start * vacanciesByFilter.getPageSize() + vacanciesByFilter.getVacancies().size(),
                    vacanciesByFilter.getTotalVacancies()));

            markup = VacanciesListPageableMarkup.getMarkup(filterId, vacanciesByFilter.getCurrentPage(),
                    vacanciesByFilter.getTotalPages());
            text = stringBuilder.toString();
        } else {
            text = "There is no vacancies by this filter";
            markup = BackInlineMarkup.getMarkup("filter-vacancies");
        }

        UserDTO user = userRequest.getUser();
        user.setUserState(UserState.FILTER_VACANCIES_LIST_STATE);
        user.addRequestCallbackData(callbackData);
        stateService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(text)
                .replyMarkup(markup)
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
