# eureka client feign hystrix(eureka feign hystrix客户端)

包含feign使用hystrix熔断器 以及hystrix熔断器监控hystrix dashboard

## 创建普通maven项目 pom.xml添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
 
## 修改配置文件

    server:
      port: 8769
    spring:
      application:
        name: eureka-client-feign-hystrix
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
    feign:
      hystrix:
        enabled: true
    management:
      endpoints:
        web:
          exposure:
            include: hystrix.stream

spring boot 2.0后需要添加management.endpoints

defaultZone不能写成default-zone

feign自带hystrix 需要在配置文件中启用feign.hystrix.enabled=true

## 配置启动类

    启动类添加注解
    @EnableHystrix
    @EnableHystrixDashboard
    @EnableFeignClients

## 编写service

    @FeignClient(value = "eureka-client", configuration = FeignConfig.class, fallback = FeignServiceError.class)
    public interface FeignService {
    
        @GetMapping("/hello")
        String hello(String name);
    }
在eureka_client_feign项目代码基础上添加fallback用于失败快速返回

FeignServiceError需要实现FeignService 重写hello方法 并加入spring ioc容器

    @Component
    public class FeignServiceError implements FeignService {
    
        @Override
        public String hello(String name) {
            return "hi " + name + " hello api error";
        }
    }

## hystrix dashboard监控页面

    访问http://localhost:8769/actuator/hystrix.stream可以看到熔断监控数据
    
    访问http://localhost:8769/hystrix输入http://localhost:8769/actuator/hystrix.stream查看监控仪表 未产生数据为loading...
    
spring boot 2.0后访问额路径为actuator/hystrix.stream 也可通过配置形式直接访问/hystrix.stream

    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean<>(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }