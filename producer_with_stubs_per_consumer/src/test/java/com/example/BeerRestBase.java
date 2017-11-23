package com.example;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.argThat;

/**
 * cdc 是不是测试功能，而是测试api、参数，是否匹配，而不是测试功能是否正常
 * 所以 service 不连接数据库，进行测试
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BeerRestBase {
    //remove::start[]

    @Mock
    PersonCheckingService personCheckingService;
    @InjectMocks
    ProducerController producerController;

    @Before
    public void setup() {
        given(personCheckingService.shouldGetBeer(argThat(oldEnough()))).willReturn(true);
        RestAssuredMockMvc.standaloneSetup(producerController);
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
