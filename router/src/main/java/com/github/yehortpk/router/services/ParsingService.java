package com.github.yehortpk.router.services;

import com.github.yehortpk.router.models.parser.ParsingProgress;
import com.github.yehortpk.router.models.parser.ParsingProgressDTO;
import com.github.yehortpk.router.repositories.ParsingProgressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParsingService {
    private final ModelMapper modelMapper;
    private final ParsingProgressRepository parsingProgressRepository;

    public Optional<ParsingProgress> findByParsingHash(String parsingHash) {
        return parsingProgressRepository.findByParsingHash(parsingHash);
    }

    public synchronized void saveParsingProgress(ParsingProgressDTO parsingProgressDTO) {
        ParsingProgress progressEntity = modelMapper.map(parsingProgressDTO, ParsingProgress.class);
        parsingProgressRepository.save(progressEntity);
    }
}
