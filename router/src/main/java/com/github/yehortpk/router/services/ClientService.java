package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.client.Client;
import com.github.yehortpk.router.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public Client findById(long clientId){
        return clientRepository.findById(clientId).orElseThrow(
                () -> new RuntimeException("Client with this id hasn't found"));
    }
}
