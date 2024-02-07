package com.github.yehortpk.subscriberbot.handlers;

import com.github.yehortpk.subscriberbot.dtos.UserDTO;
import com.github.yehortpk.subscriberbot.dtos.UserRequestDTO;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Main class that responsible for handling {@link Update} request with the list of handlers
 */
public class Dispatcher {
    private final List<RequestHandler> handlers;
    private final UserDTO user;


    public Dispatcher(List<RequestHandler> handlers, UserDTO user) {
        this.handlers = handlers;
        this.user = user;
    }

    /**
     * Check if any handler is applicable for the {@link Update} request. If so, handle it.
     *
     * @param userRequest {@link Update} user request
     */
    public void dispatch(Update userRequest) {
        // Wrap current user state and request to one DTO object
        UserRequestDTO userRequestDTO = new UserRequestDTO(user, userRequest);

        for (RequestHandler handler : handlers) {
            if (handler.isApplicable(userRequestDTO)) {
                handler.handle(userRequestDTO);
                break;
            }
        }
    }

}
