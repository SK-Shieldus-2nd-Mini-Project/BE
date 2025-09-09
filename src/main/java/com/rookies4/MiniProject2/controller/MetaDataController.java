// sk-shieldus-2nd-mini-project/be/BE-mypage_mygroups/src/main/java/com/rookies4/MiniProject2/controller/MetaDataController.java
package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.RegionDto;
import com.rookies4.MiniProject2.dto.SportDto;
import com.rookies4.MiniProject2.service.MetaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MetaDataController {

    private final MetaDataService metaDataService;

    @GetMapping("/regions")
    public ResponseEntity<List<RegionDto>> getRegions() {
        return ResponseEntity.ok(metaDataService.getAllRegions());
    }

    @GetMapping("/sports")
    public ResponseEntity<List<SportDto>> getSports() {
        return ResponseEntity.ok(metaDataService.getAllSports());
    }
}