package com.sw.multiplication.service;

import com.sw.multiplication.domain.Multiplication;
import com.sw.multiplication.domain.MultiplicationResultAttempt;

import java.util.List;

public interface MultiplicationService {
    Multiplication createRandomMultiplication();
    boolean checkAttempt(final MultiplicationResultAttempt resultAttempt);
    public List<MultiplicationResultAttempt> getStatsForUser(String userAlias);
}
