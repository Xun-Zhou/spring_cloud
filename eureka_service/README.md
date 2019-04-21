# eureka service(eureka服务)

## 创建普通maven项目 pom.xml添加依赖
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>

## 修改配置文件

    server:
      port: 8761
    
    eureka:
      instance:
        hostname: localhost
      client:
        register-with-eureka: false
        fetch-registry: false
        service-url:
          defaultZone: http://localhost:8761/eureka/
    
    spring:
      application:
        name: eureka-service
        
register-with-eureka: false和fetch-registry: false防止自己注册自己

defaultZone不能写成default-zone

## 修改启动文件
    
    启动类添加注解@EnableEurekaServer