
#  spring-boot logger 
***
## 1. Setup


~~build.gradle파일에 아래 줄 추가~~.
``` 
 dependencies {  
    implementation 'com.spring.boot.logger:logger:0.5.0-SNAPSHOT'
 }
```

Main Application Class에 @EnableAspectJAutoProxy, @EnableLogger 추가
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

application.yml에 profile 별 service, logging 추가.
   
기본 json 포맷출력 logging.type = json로 설정.   
GCP stackdriver로 내보낸다면 logging.type = stackdriver로 설정.  
Request Parameter Key값에 따라 value를 masking하는 기능 추가. ( ****** 로 replace 된다.)
```
// if profile is 'local'
// service name can be 'anibear-local' or 'minifocex-local'

service: <service-name-for-logging>
logging:
   application:  // application log를 찍고 싶다면 설정.
     on-log: true (default false)
     type: json or stackdriver  
   aws:
     credentials:
       accessKey: ${AWS_ACCESS_KEY}
       secretkey: ${AWS_SECRET_KEY}
     kinesis:   // log를 Kinesis Datastream으로 보내고 싶다면 설정.
       producer:
         produce: true (default false)
         region: ${region}
         streamName: ${streamName}
   parameters:
     masking-keys: ${key1}, ${key2} ...

```

  
logback-spring.xml console appender 설정
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

 여기서 application 로그는 API Request, Response에 대한 전체적인 로그를 말한다.  
 application 로그는 @RestController annotation을 가진 객체의 모든 메소드의 로그를 출력한다.

만약, Application Log를 남기고 싶지 않은 API에 대해서는 메소드 상위에 @NoApplicationLog 어노테이션을 적으면 된다.

```
    @NoApplicationLog
    @RequestMapping(value = "/")
    public ResponseEntity liveness() {
        return ResponseEntity.ok().build();
    }
```


## 3. General Log

General Log는 로그를 남기고 싶은 위치와 남기고 싶은 변수들을 custom하게 찍을 수 있다.  

```
    HomeService.java
    
    private final GLogger logger;
    
    public JSONObject home(JSONObject object) throws Exception {
        JSONObject loginResult = new JSONObject();
        loginResult = query.select1st(0, "PROC_USER_LOGIN_V3",
                object.get("id").toString(), -1, -1, "", -1);


        LogDTO homeLog = HomeLogDTO.HomeLog.
                builder().
                name("로그인").
                coin(loginResult.getCoin()).
                jewel(loginResult.getJewel()).
                build();

        logger.info(homeLog);   // @Alf4j 개발 또는 LogFactory로 개발 예정
        
        return loginResult;
    }
```
```
    public class HomeLogDTO {
    
        @Getter
        @SuperBuilder   // ILogDTO를 implement받으면 Builder로 안해도 된다.
        public static class HomeLog extends LogDTO {
            private Integer jewel;
            private Integer coin;
        }
    }
```

General log DTO는 com.spring.boot.logger.ILogDTO객체를 implements 받는 객체여야 한다.


## 4. Exception Class

Logger 라이브러리의 ApiException이 RuntimeException을 extends 받고있다.

``` 
    // ex )
    if (isBlank(name)) {
                               <code>, <message>
        throw new ApiException(801, "RANK PARAMETER GAME NUM ERROR");
    }
```

## 5. Filter와 Interceptor, AOP @Before 메소드 에러 처리


### 2가지 방법.  
  

#### 1. ApplicationLogger 클래스 추가.  
  
어디에서나 해당 메서드를 호출하면 에러 로그를 찍을 수 있다.
```
 private final ApplicationLogger applicationLogger;
 
 applicationLogger.error(String message); // 호출 시 에러 로그.
```
  
    
#### 2. FilterExceptionHandler 클래스. (필터 에러만 적용.)

Filter 등록 시 에러 로그 가능.
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
