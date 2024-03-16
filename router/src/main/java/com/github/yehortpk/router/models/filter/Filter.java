package com.github.yehortpk.router.models.filter;

import com.github.yehortpk.router.models.client.Client;
import com.github.yehortpk.router.models.company.Company;
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
public class Filter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name="company_id", nullable=false, updatable=false)
    private Company company;
    @ManyToOne(optional = false)
    @JoinColumn(
            name="client_id", nullable=false, updatable=false)
    private Client client;
    @Column(columnDefinition = "TEXT")
    private String filter;

    public Filter(Company company, Client client, String filter) {
        this.company = company;
        this.client = client;
        this.filter = filter;
    }
}
