package com.rookies4.MiniProject2.dto;

import com.rookies4.MiniProject2.domain.entity.Sport;
import lombok.Getter;

@Getter
public class SportDto {
    private final Integer sportId;
    private final String sportName;

    public SportDto(Sport sport) {
        this.sportId = sport.getId();
        this.sportName = sport.getSportName();
    }
}