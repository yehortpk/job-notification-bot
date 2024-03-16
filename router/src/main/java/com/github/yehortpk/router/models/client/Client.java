package com.github.yehortpk.router.models.client;

import com.github.yehortpk.router.models.company.Company;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "client")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Client {
    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    private long chatId;
    @ManyToMany(mappedBy = "subscribers")
    private List<Company> subscriptions;
}