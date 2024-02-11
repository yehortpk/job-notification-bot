package com.github.yehortpk.router.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "company")
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDAO {
    @Id
    private int companyId;
    private String jobsTemplateLink;
    private String beanClass;
    @ToString.Include
    private String title;
    @EqualsAndHashCode.Include
    private String link;
    private boolean isEnabled;
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "company_subscriber",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<ClientDAO> subscribers;

    public void addSubscription(ClientDAO client) {
        subscribers.add(client);
    }

    public void removeSubscription(ClientDAO client) {
        subscribers.remove(client);
    }
}
