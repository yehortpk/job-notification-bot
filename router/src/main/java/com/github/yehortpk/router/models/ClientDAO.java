package com.github.yehortpk.router.models;

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
public class ClientDAO {
    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    private long chatId;
    @ManyToMany(mappedBy = "subscribers")
    private List<CompanyDAO> subscriptions;
}