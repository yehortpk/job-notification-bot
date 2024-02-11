package com.github.yehortpk.router.models;

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

    public static FilterDTO fromDAO(FilterDAO dao) {
        return new FilterDTO(
                dao.getId(),
                CompanyDTO.fromDAO(dao.getCompany()),
                ClientDTO.fromDAO(dao.getClient()),
                dao.getFilter());
    }
}
