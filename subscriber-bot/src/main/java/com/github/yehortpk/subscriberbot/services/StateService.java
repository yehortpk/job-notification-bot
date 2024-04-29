package com.github.yehortpk.subscriberbot.services;

import com.github.yehortpk.subscriberbot.dtos.UserDAO;
import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.repositories.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StateService {
    private final StateRepository stateRepository;

    public void saveUser(UserDTO user) {
        stateRepository.save(user.toDAO());
    }

    public UserDTO getUser(long chatId) {
        UserDAO user = stateRepository.findById(chatId).orElseGet(() -> {
            UserDAO userDAO = new UserDAO();
            userDAO.setChatId(chatId);
            return userDAO;
        });

        return UserDTO.fromDAO(user);
    }
}
