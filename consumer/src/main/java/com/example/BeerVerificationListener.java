package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Marcin Grzejszczak
 */
@Component
class BeerVerificationListener {

	private static final Log log = LogFactory.getLog(BeerVerificationListener.class);

	AtomicInteger eligibleCounter = new AtomicInteger();
	AtomicInteger notEligibleCounter = new AtomicInteger();

	//@StreamListener 作用是将被修饰的方法注册为消息中间件上数据流的事件监听器
	//该方法将其注册为输入消息通道的监听器，
	//当输入 消息通道中有消息到达的时候，会立即触发该注解方法的处理逻辑对消息进行消费
	@StreamListener(Sink.INPUT)
	public void listen(Verification verification) {
		log.info("Received new verification: "+verification.eligible);
		//remove::start[]
		//tag::listener[]
		if (verification.eligible) {
			eligibleCounter.incrementAndGet();
		} else {
			notEligibleCounter.incrementAndGet();
		}
		//end::listener[]
		//remove::end[]
	}

	public static class Verification {
		public boolean eligible;
	}
}
