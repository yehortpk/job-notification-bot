package com.github.yehortpk.router.models.parser;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "progress")
public class ParsingProgress {
    @Id
    private String id;

    @Indexed(unique = true)
    private String parsingHash;
    private Integer parsedVacancies;
    private Integer newVacancies;
    private Boolean finished;
    private List<ParserProgress> parsers;
}
