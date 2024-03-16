package com.github.yehortpk.router.models.company;

import com.github.yehortpk.router.models.client.Client;
import com.github.yehortpk.router.models.vacancy.Vacancy;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "company")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    private int companyId;
    private String singlePageRequestLink;
    private String jobsTemplateLink;
    private String beanClass;
    @ToString.Include
    private String title;
    @EqualsAndHashCode.Include
    private String link;
    private boolean isEnabled;
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "company_subscriber",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<Client> subscribers;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<Vacancy> vacancies;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<CompanyData> companyData;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<CompanyHeader> companyHeaders;

    public void addSubscription(Client client) {
        subscribers.add(client);
    }

    public void removeSubscription(Client client) {
        subscribers.remove(client);
    }
}
