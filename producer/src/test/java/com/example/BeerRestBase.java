package com.example;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Random;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;


@RunWith(MockitoJUnitRunner.class)
public abstract class BeerRestBase {
    //remove::start[]
    @Mock
    PersonCheckingService personCheckingService;

    @Mock
    StatsService statsService;

    //测试controller
    @InjectMocks
    ProducerController producerController;
    @InjectMocks
    StatsController statsController;

    @Before
    public void setup() {
        given(personCheckingService.shouldGetBeer(argThat(oldEnough()))).willReturn(true);
        given(statsService.findBottlesByName(anyString())).willReturn(new Random().nextInt());

        //Spring Cloud Contract使用RestAssured来发送请求
        RestAssuredMockMvc.standaloneSetup(producerController, statsController);
    }

    private TypeSafeMatcher<PersonToCheck> oldEnough() {
        return new TypeSafeMatcher<PersonToCheck>() {
            @Override
            protected boolean matchesSafely(PersonToCheck personToCheck) {
                return personToCheck.age >= 20;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
    //remove::end[]
}