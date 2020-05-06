package com.sw.multiplication.service;

import com.sw.multiplication.domain.Multiplication;
import com.sw.multiplication.domain.MultiplicationResultAttempt;
import com.sw.multiplication.domain.User;
import com.sw.multiplication.event.EventDispatcher;
import com.sw.multiplication.event.MultiplicationSolvedEvent;
import com.sw.multiplication.repository.MultiplicationResultAttemptRepository;
import com.sw.multiplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService{
    private RandomGeneratorService randomGeneratorService;
    private MultiplicationResultAttemptRepository attemptRepository;
    private UserRepository userRepository;
    private EventDispatcher eventDispatcher;

    @Autowired
    public MultiplicationServiceImpl(RandomGeneratorService randomGeneratorService,
                                     MultiplicationResultAttemptRepository attemptRepository,
                                     UserRepository userRepository,
                                     EventDispatcher eventDispatcher) {
        this.randomGeneratorService = randomGeneratorService;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Multiplication createRandomMultiplication() {
        int factorA = randomGeneratorService.generateRandomFactor();
        int factorB = randomGeneratorService.generateRandomFactor();
        return new Multiplication(factorA,factorB);
    }

    @Transactional
    @Override
    public boolean checkAttempt(MultiplicationResultAttempt resultAttempt) {

        Optional<User> user = userRepository.findByAlias(resultAttempt.getUser().getAlias());

//        Assert.isTrue(resultAttempt.isCorrect(),"채점한 상태로 보낼 수 없습니다!");

        boolean isCorrect = resultAttempt.getResultAttempt() ==
                resultAttempt.getMultiplication().getFactorA() * resultAttempt.getMultiplication().getFactorB();


        MultiplicationResultAttempt checkedAttempt =
                new MultiplicationResultAttempt(
                        user.orElse(resultAttempt.getUser()),
                        resultAttempt.getMultiplication(),
                        resultAttempt.getResultAttempt(),
                        isCorrect);

        attemptRepository.save(checkedAttempt);

        eventDispatcher.send(new MultiplicationSolvedEvent(checkedAttempt.getId(),
                checkedAttempt.getUser().getId(),
                checkedAttempt.isCorrect())
        );

        return isCorrect;
    }

    @Override
    public List<MultiplicationResultAttempt> getStatsForUser(String userAlias){
        return attemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
    }
}
