package contracts.beer.rest

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a grumpy waiter that is too bored to sell any alcohol for anyone.
""")
    request {
        method POST()
        url '/buy'
        body(
                name: $(anyAlphaUnicode()),
                age: 25 //初始化值
        )
        /**
         * 如果通过匹配器部分提供值，那么添加匹配项的键的值将从自动测试断言生成中删除。
         * 您必须通过匹配器部分手动提供这些值
         *
         * 提供动态匹配 age 值是否合法
         */
        stubMatchers {
            jsonPath('$.age', byRegex('[2-9][0-9]'))
        }
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 200
        body(
                /**
                 *  Spring Cloud Contract为您提供了一个名为fromRequest()的方法，
                 *  它允许您在响应中指定要从请求中获取某些值
                 */
                message: "You're drunk [${fromRequest().body('$.name')}]. Go home!",

                /**
                 * 对于生成的测试，我们希望通过基类中定义的assertStatus()方法进行自定义断言
                 *
                 * 自定义断言为了实现这一目标，
                 * 我们需要编写 $（c('NOT_OK')、 p(execute('assertStatus($it)')))
                 *
                 * c()是consumer()的快捷方式，p()是producer()的缩写。通过调用$(c()，p())，
                 * 我们为消费者提供了一个具体的值，并为生产者提供了一个动态的值。

                 $(c("NOT_OK")，...)意味着，对于stub中的响应，对于状态字段，我们希望存根包含NOT_OK的值。

                 $(...,p(execute('assertStatus($it)')))
                 意味着我们希望在生成器方面在自动生成测试中运行基类中定义的方法.该方法称为assertStatus()。
                 作为该方法的参数，我们要传递响应JSON中存在的元素的值。

                 在我们的例子中，我们为$.status字段提供一个动态值。
                 assertStatus($it)被转换为 assertStatus(从 JSON 响应读取 $.status)
                 *
                 */
                status: $(c("NOT_OK"), p(execute('assertStatus($it)')))
        )

        /**
         * 现在我们可能想message通过一种方法来对该领域进行一些更复杂的分析assertMessage()。
         * 还有另一种方法：我们可以调用该 testMatchers部分。
         *
         * 根据 testMatchers，我们可以定义，通过 JSON 的路径，我们想要动态地断言的元素
         */
        testMatchers {
            jsonPath('$.message', byCommand('assertMessage($it)'))
        }
        headers {
            contentType(applicationJson())
        }

        // 因为生产者端，controller 使用了异步,所以添加此方法告诉契约，servlet是异步的
        async()
    }
    /**
     * 我们看到一个星巴克人总是要得到啤酒。因此，我们需要一个特定的通用模式。
     * 这就是为什么我们将优先级设置为100（数字越高，优先级越低）
     */
    priority 100
}
