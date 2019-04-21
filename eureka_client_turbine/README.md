# eureka client turbine(eureka ribbon turbine客户端)

hystrix turbine聚合熔断器监控

## 创建普通maven项目 pom.xml添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
 
## 修改配置文件

    server:
      port: 8770
    spring:
      application:
        name: eureka-client-turbine
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
    turbine:
      aggregator:
        cluster-config: default
      app-config: eureka-client-ribbon-hystrix,eureka-client-feign-hystrix
      cluster-name-expression: new String("default")

defaultZone不能写成default-zone

turbine.aggregator.cluster-config可以不填写 默认default

turbine.aggregator.app-config配置需要聚合的服务名称逗号分隔

## 配置启动类

    启动类添加注解
    @EnableTurbine

## 监控页面

    访问http://localhost:8770/turbine.stream可以看到监控数据
    
    访问http://localhost:8768/hystrix输入http://localhost:8770/turbine.stream可以看到监控仪表数据