package com.rookies4.MiniProject2.dto;

import com.rookies4.MiniProject2.domain.entity.Region;
import lombok.Getter;

@Getter
public class RegionDto {
    private final Integer regionId;
    private final String regionName;

    public RegionDto(Region region) {
        this.regionId = region.getId();
        this.regionName = region.getRegionName();
    }
}