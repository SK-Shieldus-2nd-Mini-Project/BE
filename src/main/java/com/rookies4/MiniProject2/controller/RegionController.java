package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.domain.entity.Region;
import com.rookies4.MiniProject2.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {
    private final RegionRepository regionRepository;

    @GetMapping
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }
}
