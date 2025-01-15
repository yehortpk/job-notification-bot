package com.github.yehortpk.router.repositories;

import com.github.yehortpk.router.models.parser.ParsingProgress;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ParsingHistoryRepository extends MongoRepository<ParsingProgress, String> {
    Optional<ParsingProgress> findByParsingHash(String parsingHash);
}
