![Generic Badge](https://img.shields.io/badge/version-0.1.2-success.svg)
![Generic badge](https://img.shields.io/badge/license-Apache--2.0-orange)

#  Spring-Boot-Logger

JSON Type Logging Library using Spring AOP, Logback.  
You can print your application log or what you want to, also send to Kinesis Data Streams.  

- Java 11 +

## 1. Setup

Add @EnableAspectJAutoProxy, @EnableLogger in your main class
```
@EnableAspectJAutoProxy
@EnableLogger
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### application.yaml settings.

```
logging:
   service: <service-name-for-logging>
   application:                        // if you want to print application log.
     on-log: true (default false)
     type: json or stackdriver         // normal json or GCP stackdriver
   aws:                                // if you want to send your log to Kinesis Data Streams.
     credentials:
       accessKey: ${AWS_ACCESS_KEY}
       secretkey: ${AWS_SECRET_KEY}
     kinesis:
       producer:
         produce: true (default false)
         region: ${region}
         streamName: ${streamName}
   parameters:                         // if need to hide value from request. 
     masking-keys: ${key1}, ${key2} ...

```

  
### logback-spring.xml console appender settings.
```
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
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
Just create your Object Class and implements `ILogDTO`.

**Examples :**
```
 
@Getter
@SuperBuilder
public class LogDTO implements ILogDTO {
    private String name;
    private Integer code;
}

```
```
    public class LoginLogDTO {
    
        @Getter
        @SuperBuilder
        public static class HomeLog extends LogDTO {
            private Long id;
            private Integer coin;
        }
    }
```
``` 
    private final Logger logger;
    
    public JSONObject login(JSONObject object) throws Exception {
        JSONObject loginResult = new JSONObject();
        loginResult = query.select(<sql:>, value);


        LogDTO loginLog = LoginLogDTO.LoginLog.
                builder().
                name("login").
                code(100201).
                id(loginResult.getId()).
                coin(loginResult.getCoin()).
                build();                      // You don't have to do with builder patterns. Just create your own class and implements ILogDTO. 

        logger.info(loginLog); 
        
        return loginResult;
    }
```

## 4. How to print log if exception in Filter, Interceptor and @Before Methods?
  

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
