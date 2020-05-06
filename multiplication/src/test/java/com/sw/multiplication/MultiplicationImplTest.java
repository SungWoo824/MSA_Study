package com.sw.multiplication;

import com.sw.multiplication.domain.Multiplication;
import com.sw.multiplication.domain.MultiplicationResultAttempt;
import com.sw.multiplication.domain.User;
import com.sw.multiplication.repository.MultiplicationResultAttemptRepository;
import com.sw.multiplication.repository.UserRepository;
import com.sw.multiplication.service.MultiplicationServiceImpl;
import com.sw.multiplication.service.RandomGeneratorService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class MultiplicationImplTest {
    private MultiplicationServiceImpl multiplicationService;

    @Mock
    private RandomGeneratorService randomGeneratorService;

    @Mock
    private MultiplicationResultAttemptRepository attemptRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        multiplicationService = new MultiplicationServiceImpl(randomGeneratorService,attemptRepository,userRepository);
    }

    @Test
    public void createRandomMultiplicationTest(){
        given(randomGeneratorService.generateRandomFactor()).willReturn(50,30);

        Multiplication multiplication = multiplicationService.createRandomMultiplication();

        assertThat(multiplication.getFactorA()).isEqualTo(50);
        assertThat(multiplication.getFactorB()).isEqualTo(30);

    }

    @Test
    public void checkCorrectAttemptTest(){
        Multiplication multiplication = new Multiplication(50,60);
        User user = new User("Eric");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user,multiplication,3000,false);

        MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(user,multiplication,3000,true);
        given(userRepository.findByAlias("Eric")).willReturn(Optional.empty());

        boolean attemptResult = multiplicationService.checkAttempt(attempt);

        assertThat(attemptResult).isTrue();
        verify(attemptRepository).save(verifiedAttempt);
    }

    @Test
    public void checkWrongAttemptTest(){
        Multiplication multiplication = new Multiplication(50,60);
        User user = new User("Eric");

        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user,multiplication,3010,false);
        given(userRepository.findByAlias("Eric")).willReturn(Optional.empty());

        boolean attemptResult = multiplicationService.checkAttempt(attempt);

        assertThat(attemptResult).isFalse();
        verify(attemptRepository).save(attempt);
    }

    @Test
    public void retrieveStatsTest(){
        Multiplication multiplication = new Multiplication(50,60);
        User user = new User("Eric");
        MultiplicationResultAttempt attempt1 =
                new MultiplicationResultAttempt(user,multiplication,3010,false);
        MultiplicationResultAttempt attempt2 =
                new MultiplicationResultAttempt(user,multiplication,3051,false);
        List<MultiplicationResultAttempt> latestAttemts = Lists.newArrayList(attempt1,attempt2);
        given(userRepository.findByAlias("Eric")).willReturn(Optional.empty());
        given(attemptRepository.findTop5ByUserAliasOrderByIdDesc("Eric")).willReturn(latestAttemts);

        List<MultiplicationResultAttempt> latestAttemptsResult = multiplicationService.getStatsForUser("Eric");

        assertThat(latestAttemptsResult).isEqualTo(latestAttemts);
    }

}
