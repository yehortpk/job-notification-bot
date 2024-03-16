package com.github.yehortpk.router.models.filter;

import com.github.yehortpk.router.models.client.ClientDTO;
import com.github.yehortpk.router.models.company.CompanyDTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class FilterDTO {
    private long id;
    private CompanyDTO company;
    private ClientDTO client;
    private String filter;
}
