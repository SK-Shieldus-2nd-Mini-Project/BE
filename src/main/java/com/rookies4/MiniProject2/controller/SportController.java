package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.domain.entity.Sport;
import com.rookies4.MiniProject2.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportController {
    private final SportRepository sportRepository;

    @GetMapping
    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }
}