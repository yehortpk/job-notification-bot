package com.github.yehortpk.router.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "filter")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class FilterDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name="company_id", nullable=false, updatable=false)
    private CompanyDAO company;
    @ManyToOne(optional = false)
    @JoinColumn(
            name="client_id", nullable=false, updatable=false)
    private ClientDAO client;
    @Column(columnDefinition = "TEXT")
    private String filter;

    public FilterDAO(CompanyDAO companyDAO, ClientDAO clientDAO, String filter) {
        this.company = companyDAO;
        this.client = clientDAO;
        this.filter = filter;
    }
}
