package com.github.yehortpk.router.models.client;

import com.github.yehortpk.router.models.company.CompanyDTO;
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
}
