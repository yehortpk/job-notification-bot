package com.github.yehortpk.router.models;

import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ClientDTO {
    private long chatId;
    private Set<CompanyDTO> subscriptions;

    public static ClientDTO fromDAOWithoutSubscriptions(ClientDAO dao) {
        return new ClientDTO(dao.getChatId(), new HashSet<>());
    }

    public static ClientDTO fromDAO(ClientDAO dao) {
        return new ClientDTO(dao.getChatId(), dao.getSubscriptions().stream()
                .map(CompanyDTO::fromDAOWithoutClients)
                .collect(Collectors.toSet()));
    }
}
