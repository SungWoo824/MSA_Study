package com.sw.multiplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.multiplication.controller.MultiplicationResultController;
import com.sw.multiplication.domain.Multiplication;
import com.sw.multiplication.domain.MultiplicationResultAttempt;
import com.sw.multiplication.domain.User;
import com.sw.multiplication.service.MultiplicationService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(MultiplicationResultController.class)
public class MultiplicationResultControllerTest {

    @MockBean
    private MultiplicationService multiplicationService;


    @Autowired
    private MockMvc mvc;

    private JacksonTester<MultiplicationResultAttempt> jsonResult;
    private JacksonTester<MultiplicationResultAttempt> jsonResponse;

    private JacksonTester<MultiplicationResultAttempt> jsonResultAttempt;
    private JacksonTester<List<MultiplicationResultAttempt>> jsonResultAttemptList;

    @BeforeEach
    public void setUp(){
        JacksonTester.initFields(this,new ObjectMapper());
    }

    @Test
    public void postResultReturnCorrect() throws Exception{
        genericParmeterizedTest(true);
    }

    @Test
    public void postResultReturnNotCorrect() throws Exception{
        genericParmeterizedTest(false);
    }

    void genericParmeterizedTest(final boolean correct) throws Exception{
        given(multiplicationService.checkAttempt(any(MultiplicationResultAttempt.class))).willReturn(correct);

        User user = new User("Eric");
        Multiplication multiplication = new Multiplication(50,70);
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user,multiplication,3500,correct);

        MockHttpServletResponse response = mvc.perform(
                post("/result").contentType(MediaType.APPLICATION_JSON).content(jsonResult.write(attempt).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString())
                .isEqualTo(jsonResponse.write(new MultiplicationResultAttempt(
                        attempt.getUser(),attempt.getMultiplication(),attempt.getResultAttempt(),correct)).getJson());
    }

    @Test
    public void getUserStats() throws Exception{
        User user = new User("Eric");
        Multiplication multiplication = new Multiplication(50,70);
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(
                user,multiplication,3500,true);
        List<MultiplicationResultAttempt> recentAttempts = Lists.newArrayList(attempt,attempt);
        given(multiplicationService.getStatsForUser("Eric")).willReturn(recentAttempts);

        MockHttpServletResponse response = mvc.perform(get("/result").param("alias","Eric"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonResultAttemptList.write(recentAttempts).getJson());
    }
}
