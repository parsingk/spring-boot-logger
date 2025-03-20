![Generic Badge](https://img.shields.io/badge/version-1.1.0-success.svg)
![Generic badge](https://img.shields.io/badge/license-Apache--2.0-orange)

#  Spring-Boot-Logger

JSON Type Logging Library using Spring AOP, Logback.  
You can print your application log or what you want to, also send to Kinesis Data Streams.  
This is based on java 17 and spring boot 3.

## 1. Setup

Add @EnableAspectJAutoProxy in your main class
```java
@EnableAspectJAutoProxy
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```


### Log Configuration Settings.

```java
@Configuration
public class LogConfig {

    @Bean
    public LoggerConfig config() throws Exception {
        LoggerConfig config = new LoggerConfigBuilder(${service})
            .generalLoggerIncludeHeaders(${includeHeaders}) // generalLogger header 값 포함 여부. default true.
            .applicationLoggerBean(${Bean Class}) // 로그 명세서.
            .applicationLogger(${Logger}) // aop doAround 함수 구현한 로거 객체.
            .maskingKeys(${List Object})
            .objectMapper(${Custom ObjectMapper Object}) // 
            .build();
        
        config.setAwsKinesisConfig(new KinesisConfigBuilder()   // kinesis data streams configuration.
                .region(${region})
                .streamName(${stream-name})
                .logType(AwsKinesisLogType.BOTH)    // kinesis로 인게임만 보낼지, 시스템로그만 보낼지, 둘다 보낼지.
                .build());

        config.onKinesisStream(${access-key}, ${secret});


        return config;
    }
}

```
  
<br>

- Service : This is your service name.
- IncludeHeaders : Set `true`, If you want to print with request headers in general log. (default `true`)
- ApplicationLogger : You can create custom logger. implement business logic. 
- ApplicationLoggerBean : It is custom logger bean what you want to print for log.
- MaskingKeys : For hiding request information. it will replace to `******`.
- AwsKinesisConfig, onKinesisStream : You can send log to AWS Kinesis Streams. 

<br>
  
### logback-spring.xml console appender settings.
```xml
    <appender name="${your-appender-name}" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <charset>UTF-8</charset>
            <layout class="com.spring.boot.logger.LoggerLayout">
                <timestampFormat>yyyy-MM-dd'T'HH:mm:ss.SSSX</timestampFormat>
                <timestampFormatTimezoneId>Etc/UTC</timestampFormatTimezoneId>
                <jsonFormatter class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
                    <prettyPrint>false</prettyPrint>
                </jsonFormatter>
                <appendLineSeparator>true</appendLineSeparator>
            </layout>
        </encoder>
    </appender>
```

## 2. Application Log

 It is print API Request, Response Log by @RestController annotation.  
If you do not want print log, use @NoApplicationLog on your method. 

```java
    @NoApplicationLog
    @RequestMapping(value = "/")
    public ResponseEntity liveness() {
        return ResponseEntity.ok().build();
    }
```


## 3. General Log

It is for your custom log.
You can print whatever, wherever.

**Examples :**  
You don't have to do with builder patterns. Just create your own class and implements `ILogDTO`.  
Declare `IGeneralLogger` and use `log()` method. It is only support log level `INFO`.

아래는 예시로 `ILogDTO`를 implement 받기만하고 커스텀하게 처리해도 된다.  
필자도 아래처럼 쓰고 있진않다. 하나의 방법이다.
```java
 
@Getter
@SuperBuilder
public class LogDTO implements ILogDTO {
    private String name;
    private Integer code;
}

```
```java
    public class AuthLogDTO {
    
        @Getter
        @SuperBuilder
        public static class LoginLog extends LogDTO {
            private Long id;
            private Integer coin;
        }
    }
```
```java
    private final IGeneralLogger logger;
    
    public JSONObject login(JSONObject object) throws Exception {
        JSONObject loginResult = new JSONObject();
        loginResult = query.select(<sql:>, value);


        LogDTO loginLog = AuthLogDTO.LoginLog.
                builder().
                name("login").
                code(100201).
                id(loginResult.getId()).
                coin(loginResult.getCoin()).
                build();                      

        logger.log(loginLog); 
        
        return loginResult;
    }
```
## 4. Exceptions
**Recommend to use `ApiException` class or Extend.**

<br>  

## 5. How to print log if exception in Filter, Interceptor and @Before Methods?
  

#### 1. Use ApplicationLogger.  (Recommended)

```java
 private final ApplicationLogger applicationLogger;
 
 applicationLogger.info(String message); 
 
 applicationLogger.error(String message);
```

<br>  

