package com.github.yehortpk.router.models.filter;

import com.github.yehortpk.router.models.client.Client;
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
            name="client_id", nullable=false, updatable=false)
    private Client client;

    @Column(columnDefinition = "TEXT")
    private String filter;
}
