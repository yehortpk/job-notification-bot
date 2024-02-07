package com.github.yehortpk.subscriberbot.services;

import com.github.yehortpk.subscriberbot.dtos.VacancyNotificationDTO;
import com.github.yehortpk.subscriberbot.utils.TelegramServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifierService {
    @Autowired
    TelegramServiceUtil telegramServiceUtil;

    public void notifyUser(VacancyNotificationDTO notification) {
        long chatId = notification.getChatId();
        telegramServiceUtil.sendMessageWithoutMarkup(chatId, getFormattedMessage(notification));
    }

    private String getFormattedMessage(VacancyNotificationDTO vacancy){
        StringBuilder stringBuilder = new StringBuilder();
        String titlePart = String.format("%s в %s\n", vacancy.getVacancyTitle(), vacancy.getCompanyTitle());
        stringBuilder.append(titlePart);

        String salaryPart = "Salary: " + (vacancy.getMaxSalary() == 0 ? "not specified":
                vacancy.getMinSalary() == 0 ?
                        String.format("%s", vacancy.getMaxSalary()):
                        String.format("%s - %s", vacancy.getMinSalary(), vacancy.getMaxSalary())) + "\n";
        stringBuilder.append(salaryPart);

        String linkPart = String.format("<a href='%s'>%s</a>\n", vacancy.getLink(), vacancy.getLink());
        stringBuilder.append(linkPart);

        return stringBuilder.toString();
    }
}
