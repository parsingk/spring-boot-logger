![Generic Badge](https://img.shields.io/badge/version-1.0.0-success.svg)
![Generic badge](https://img.shields.io/badge/license-Apache--2.0-orange)

#  Spring-Boot-Logger

JSON Type Logging Library using Spring AOP, Logback.  
You can print your application log or what you want to, also send to Kinesis Data Streams.  

- Java 11 +
- Java 17

## 1. Setup

Add @EnableAspectJAutoProxy in your main class
```
@EnableAspectJAutoProxy
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```


### Log Configuration Settings.

```
@Configuration
public class LogConfig {

    @Bean
    public LoggerConfig config() throws Exception {
        LoggerConfig config = new LoggerConfig(${service});
        config.setGeneralLoggerIncludeHeaders(${includeHeaders});
        config.setApplicationLoggerBean(${Bean Class});
        config.setApplicationLogger(${Logger});
        config.setMaskingKeys(${List Object});
        config.setAwsKinesisConfig(new KinesisConfigBuilder()
                .region(${region})
                .streamName(${stream-name})
                .logType(AwsKinesisLogType.BOTH)
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
```
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

```
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
```
 
@Getter
@SuperBuilder
public class LogDTO implements ILogDTO {
    private String name;
    private Integer code;
}

```
```
    public class AuthLogDTO {
    
        @Getter
        @SuperBuilder
        public static class LoginLog extends LogDTO {
            private Long id;
            private Integer coin;
        }
    }
```
``` 
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
Recommend to use `ApiException` class or Extend.

<br>  

## 5. How to print log if exception in Filter, Interceptor and @Before Methods?
  

#### 1. Use ApplicationLogger.  (Recommended)

```
 private final ApplicationLogger applicationLogger;
 
 applicationLogger.info(String message); 
 
 applicationLogger.error(String message);
```

<br>  

### OR 

<br>

#### 2. FilterExceptionHandler (Only in Filter)

```
  private final ApplicationLogger applicationLogger;

  @Bean
  public FilterRegistrationBean<FilterExceptionHandler> filterExceptionHandlerBean() {
      FilterRegistrationBean<FilterExceptionHandler> bean = new FilterRegistrationBean<>();
      bean.setFilter(new FilterExceptionHandler(applicationLogger));
      bean.setOrder(-1);

      return bean;
  }

```
