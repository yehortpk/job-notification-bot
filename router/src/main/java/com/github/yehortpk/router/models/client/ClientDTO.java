package com.github.yehortpk.router.models.client;

import com.github.yehortpk.router.models.filter.FilterDTO;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ClientDTO {
    private long chatId;
    private Set<FilterDTO> filters;
}
