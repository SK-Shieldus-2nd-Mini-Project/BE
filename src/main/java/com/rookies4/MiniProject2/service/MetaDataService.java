package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.dto.RegionDto;
import com.rookies4.MiniProject2.dto.SportDto;
import com.rookies4.MiniProject2.repository.RegionRepository;
import com.rookies4.MiniProject2.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetaDataService {

    private final RegionRepository regionRepository;
    private final SportRepository sportRepository;

    public List<RegionDto> getAllRegions() {
        return regionRepository.findAll().stream()
                .map(RegionDto::new)
                .collect(Collectors.toList());
    }

    public List<SportDto> getAllSports() {
        return sportRepository.findAll().stream()
                .map(SportDto::new)
                .collect(Collectors.toList());
    }
}