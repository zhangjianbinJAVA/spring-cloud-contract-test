package contracts.beer.messaging

import com.example.ProducerUtils
import org.springframework.cloud.contract.spec.Contract

// only to see if it works on both sides
ProducerUtils producerUtils = new ProducerUtils()

Contract.make {
	description("""
Sends a positive verification message when person is eligible to get the beer
发送一个积极的验证消息当人有资格得到啤酒

```
given:
	client is old enough
when:
	he applies for a beer
then:
	we'll send a message with a positive verification
```

""")
	// Label by means of which the output message can be triggered 通过它可以触发输出消息
	label 'accepted_verification'
	// input to the contract 输入合同
	input {
		// the contract will be triggered by a method 该合同将触发的方法
		triggeredBy('clientIsOldEnough()')
	}
	// output message of the contract 输出消息的合同
	outputMessage {
		// destination to which the output message will be sent
		// 我们要将消息发送到verifications通道（channel）
		sentTo 'verifications'
		// the body of the output message 输出消息的主体
		body(
			eligible: true
		)
		headers {
			header("contentType", applicationJsonUtf8())
		}
	}
}
