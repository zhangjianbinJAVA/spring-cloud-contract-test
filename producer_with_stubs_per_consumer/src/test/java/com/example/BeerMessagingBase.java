package com.example;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * 测试类，自动生成
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProducerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
//remove::start[]
@AutoConfigureMessageVerifier
//remove::end[]
public abstract class BeerMessagingBase {
    //remove::start[]
    @Inject
    MessageVerifier messaging;
    //remove::end[]
    @Autowired
    PersonCheckingService personCheckingService;

    @Before
    public void setup() {
        // let's clear any remaining messages
        // output == destination or channel name
        //remove::start[]
        this.messaging.receive("output", 100, TimeUnit.MILLISECONDS);
        //remove::end[]
    }

    /**
     * mq 触发这个方法，执行 personCheckingService 发送 mq
     */
    public void clientIsOldEnough() {
        //remove::start[]
        personCheckingService.shouldGetBeer(new PersonToCheck(25));
        //remove::end[]
    }

    public void clientIsTooYoung() {
        //remove::start[]
        personCheckingService.shouldGetBeer(new PersonToCheck(5));
        //remove::end[]
    }

}
