package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * @author Marcin Grzejszczak
 * <p>
 * 编写缺少的消费者消息传递实现
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
//remove::start[]
@AutoConfigureStubRunner(workOffline = true, ids = "com.example:beer-api-producer")
//remove::end[]
@DirtiesContext
public class BeerVerificationListenerTest extends AbstractTest {

    //remove::start[]
    @Autowired
    StubTrigger stubTrigger;//来触发消息
    //remove::end[]
    @Autowired
    BeerVerificationListener listener;

    //tag::listener_test[]
    @Test
    public void should_increase_the_eligible_counter_when_verification_was_accepted() throws Exception {
        int initialCounter = listener.eligibleCounter.get();

        /**
         * 指定触发 mq存根发送消息给消费者
         */
        //remove::start[]
        stubTrigger.trigger("accepted_verification");//触发带有给定标签的消息
        //remove::end[]

        then(listener.eligibleCounter.get()).isGreaterThan(initialCounter);
    }

    @Test
    public void should_increase_the_noteligible_counter_when_verification_was_rejected() throws Exception {
        int initialCounter = listener.notEligibleCounter.get();

        //remove::start[]
        stubTrigger.trigger("rejected_verification");
        //remove::end[]

        then(listener.notEligibleCounter.get()).isGreaterThan(initialCounter);
    }
    //end::listener_test[]
}
