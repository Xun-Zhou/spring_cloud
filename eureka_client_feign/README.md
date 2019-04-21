# eureka client feign(eureka feign客户端)

## 创建普通maven项目 pom.xml添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
 
## 修改配置文件

    server:
      port: 8767
    spring:
      application:
        name: eureka-client-feign
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/

defaultZone不能写成default-zone

## 启用FeignClient

    启动类添加@EnableFeignClients注解

## 编写service

    @FeignClient(value = "eureka-client", configuration = FeignConfig.class)
    public interface FeignService {
    
        @GetMapping("/hello")
        String hello(String name);
    }
feign为声明式接口 在接口上添加@FeignClient注解(value:要请求的服务名 configuration:feign配置 feign提供默认配置 覆盖配置类实现定制功能)

    @Configuration
    public class FeignConfig {
    
        @Bean
        Retryer retryer(){
            return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1L), 5);
        }
    }

## 编写controller

    @RestController
    public class HelloController {
    
        @Autowired
        private FeignService feignService;
    
        @RequestMapping("/hello")
        public String hello(String name){
            return feignService.hello(name);
        }
    }

## 使用其他http client
    
feign不直接处理请求，而是对注解进行解析，生成请求模板再交于http client处理
    
feign默认http client为HttpURLConnection，可以选择的有HttpClient和OkHttp
    
使用HttpClient只需引入依赖
        
        <dependency>
            <groupId>com.netflix.feign</groupId>
            <artifactId>feign-httpclient</artifactId>
            <version>${version}</version>
        </dependency>
使用OkHttp只需引入依赖
        
        <dependency>
            <groupId>com.netflix.feign</groupId>
            <artifactId>feign-okhttp</artifactId>
            <version>${version}</version>
        </dependency>