package com.sw.gamification.service;

import com.sw.gamification.domain.GameStats;

public interface GameService {
    GameStats newAttemptForUser(Long userId,Long attemptId, boolean correct);
    GameStats retrieveStatsForUser(Long userId);

}
