package com.sw.gamification.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public final class LeaderBoardRow {

    private final Long userId;
    private final Long totalScore;

    public LeaderBoardRow(){
        this(0L,0L);
    }
}
