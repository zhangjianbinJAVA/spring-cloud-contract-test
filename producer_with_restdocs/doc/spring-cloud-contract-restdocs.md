### Rest Doc 与 spring cloud contract 存根集成
- 使用Rest文档生成存根
- 使用Rest Docs生成契约
- 通过Spring Cloud Contract WireMock从classpath读取存根
- 通过Stub Runner从classpath读取存根

- 添加 rest Docs依赖
````xml
 <dependency>
     <groupId>org.springframework.restdocs</groupId>
     <artifactId>spring-restdocs-mockmvc</artifactId>
     <optional>true</optional>
 </dependency>
````
- 为了使用Spring Cloud Contract Rest Docs集成，您必须添加 spring-cloud-contract-wiremock依赖
> 这样可以从Rest Docs测试中生成 WireMock存根。
````xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-contract-wiremock</artifactId>
    <scope>test</scope>
</dependency>
````
- 编写DSL协议，我们可以添加必要的Spring Cloud Contract依赖
````xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-contract-verifier</artifactId>
	<scope>test</scope>
</dependency>
````

- Spring Cloud Contract插件存储测试
- Assembly 生成jar存根，jar中包含存根

### 第一个文件测试
ProducerControllerTests 添加 rest doc支持
````java
@AutoConfigureRestDocs(outputDir = "target/snippets");
````
Rest Doc生成的任何代码段都将在target/snippets文件夹中  

实现一个假的 PersonCheckingService，因为不想访问任何数据库，需要做点配置  
````java
   @Configuration
    @EnableAutoConfiguration
    static class Config {
        //remove:start[]
        @Bean
        PersonCheckingService personCheckingService() {
            return personToCheck -> personToCheck.age >= 20;
        }
        //remove::end[]

        @Bean
        ProducerController producerController(PersonCheckingService service) {
            return new ProducerController(service);
        }
    }
````

我们使用MockMvc向/check端点发送一个JSON请求  
现在还没有创建任何存根，现在解决它。  
Spring Cloud Contract WireMock提供了一种方便的方法 WireMockRestDocs.verify()  
- 注册请求和响应作为存根存储(json文件)和adoc文档。
- 通过jsonPath方法断言JSON路径的请求(这就是如何检查动态的响应)。
- 通过contentType()方法检查请求的内容类型。
- 通过stub()方法将存储的请求和响应信息保存为WireMock存根。
- 访问WireMock的API，通过WireMock()方法执行进一步的请求验证。 
  

Spring Cloud Contract WireMock还提供了一种 SpringCloudContractRestDocs.dslContract()方法  
您可以从Rest Docs测试中生成DSL契约

### 注意  
必须先调用 WireMockRestDocs.verify()方法，然后调用该SpringCloudContractRestDocs.dslContract()方法  


## 以上为 http 契约生成

### 编写mq 消费者 契约
移动到 src/test/resources/contracts/beer/messaging文件夹  
- 定义契约 
- ./mvnw clean install
- 看看src/main/resources/application.yml文件是否包含正确的目的主题设置
- 让我们转到BeerMessagingBase测试类
    - 添加 @AutoConfigureMessageVerifier 注解，mq消息与契约的相关配置
    - 发送mq消息 source.output().send(MessageBuilder.withPayload(new Verification(true)).build())
    - 向output频道发送消息（绑定到verifications主题)
- /mvnw clean install  注意要执行测试
- 查看 beer-api-producer-restdocs-0.0.1-SNAPSHOT-stubs.jar


### 展示了stubs 的不同方法
- 使用@AutoConfigureWireMock 注释手动传递从类路径注册的存根列表。  
- 使用@AutoConfigureStubRunner 注释进行离线工作
    

### 使用Spring Cloud Contract WireMock从Classpath读取HTTP Stub
> 在IDE中，从consumer_with_restdocs目录中打开消费者代码 做 TDD
- 添加依赖
````xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-contract-stub-runner</artifactId>
	<scope>test</scope>
</dependency>
````
- 现在我们将把生产者存根依赖项添加到我们的项目中
````xml
<!-- 注意：记住不要包含任何传递依赖。我们只想导入包含合同和存根的JAR-->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>beer-api-producer-restdocs</artifactId>
    <classifier>stubs</classifier>
    <version>0.0.1-SNAPSHOT</version>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>*</groupId>
            <artifactId>*</artifactId>
        </exclusion>
    </exclusions>
</dependency>
````

- BeerControllerTest类 
  添加注解  
  ````
  @AutoConfigureWireMock(stubs = "classpath:/META-INF/com.example/beer-api-producer-restdocs/**/*.json", port = 8097)
  ````  
  该注解告诉WireMock在端口启动假HTTP服务器，8090并在classpath位置注册所有存根
  
  
  
### 使用Spring Cloud Contract Stub Runner从Classpath读取HTTP Stub
- 打开BeerControllerClasspathTest。我们使用Stub Runner从类路径中选择存根
- 添加注解
````
@AutoConfigureStubRunner(ids = "com.example:beer-api-producer-restdocs:+:8090")

对于本示例，我们默认扫描以下位置：
***/META-INF/com.example/beer-api-producer-restdocs/
  /contracts/com.example/beer-api-producer-restdocs/
 /mappings/com.example/beer-api-producer-restdocs/*/.*
````

### mq 消息传递测试
- BeerVerificationListenerTest测试类
- 接收消息监听，设置spring.cloud.stream.bindings.input.destination=verifications
- 测试

### 使用Spring Cloud Contract Stub Runner 读取消息存根
>由于Rest Doc与消息传递无关，因此我们必须使用标准的Stub Runner方法
- 下载存根
````
@AutoConfigureStubRunner(workOffline = true, ids = "com.example:beer-api-producer-restdocs")
下载最新的存根com.example:beer-api-producer-restdocs，分类器stubs，如果JAR包含HTTP存根，然后在随机端口注册它们
````
- 触发消息,只需添加@Autowired StubTrigger stubTrigger字段到您的测试
- 在生产者方面的合同中，我们描述了2个标签。accepted_verification和rejected_verification
- 运行测试


### 使用Spring Cloud Contract Stub Runner从Classpath读取消息存根
- BeerVerificationListenerClasspathTest 类
- @AutoConfigureStubRunner(ids = "com.example:beer-api-producer-restdocs")
- 测试


