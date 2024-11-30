package com.github.yehortpk.router.models.client;

import com.github.yehortpk.router.models.filter.Filter;
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
    @OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
    private List<Filter> filters;
}