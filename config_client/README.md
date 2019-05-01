# spring cloud config client

## 添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

## 修改配置文件
    
    server:
      port: 8772
    spring:
      cloud:
        config:
          #uri: http://localhost:8771
          fail-fast: true
          discovery:
            enabled: true
            service-id: config-service
      profiles:
        active: dev
      application:
        name: config-client
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
    management:
      endpoints:
        web:
          exposure:
            include: refresh

配置文件需要使用bootstrap.yml  bootstrap会优先于application加载

config client获取配置文件名一般为${application.name}-${profiles.active}

## 配置刷新

    @RestController
    @RefreshScope
    public class HelloController {
    
        @Value("${app.id}")
        private String appId;
    
        @RequestMapping("/hello")
        public String hello(){
            return appId;
        }
    }

在需要刷新配置的类上添加@RefreshScope

请求http://localhost:8772/actuator/refresh  请求方式post  请求头Content-Type=application/json

使用git可以配合Webhooks进行刷新 集群环境可以配合消息队列批量刷新