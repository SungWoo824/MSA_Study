package com.sw.gamification.repository;

import com.sw.gamification.domain.LeaderBoardRow;
import com.sw.gamification.domain.ScoreCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScoreCardRepository extends CrudRepository<ScoreCard , Long> {
    @Query("SELECT sum(s.score) FROM com.sw.gamification.domain.ScoreCard s where s.userId = :userId group by s.userId")
    int getTotalScoreForUser(@Param("userId") final Long userId);

    @Query("select NEW com.sw.gamification.domain.LeaderBoardRow(s.userId, sum(s.score)) FROM com.sw.gamification.domain.ScoreCard s " +
            "GROUP BY s.userId order by sum(s.score) DESC")
    List<LeaderBoardRow> findFirst10();

    List<ScoreCard> findByUserIdOrderByScoreTimestampDesc(final Long userId);
}
