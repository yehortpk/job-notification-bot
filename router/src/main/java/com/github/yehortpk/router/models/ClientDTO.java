package com.github.yehortpk.router.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ClientDTO {
    @EqualsAndHashCode.Include
    @ToString.Include
    private long chatId;

    public static ClientDTO fromDAO(ClientDAO dao) {
        return new ClientDTO(dao.getChatId());
    }

    public ClientDAO toDAO() {
        ClientDAO clientDAO = new ClientDAO();
        clientDAO.setChatId(this.getChatId());
        return clientDAO;
    }
}
