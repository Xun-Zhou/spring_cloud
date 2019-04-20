# eureka service cluster(eureka服务集群)

资源有限 这里搭建单机伪集群
在hosts文件中添加
    
    127.0.0.1 peer1
    127.0.0.1 peer2
    127.0.0.1 peer3

## 创建普通maven项目，pom.xml添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>

## 修改配置文件

这里使用一个yml文件做三段配置，运行时使用 --spring.profiles.active=xxx 指定使用哪段配置

    spring:
      application:
        name: eureka_service_cluster
      profiles:
        active: peer1
    ---
    server:
      port: 8761
    spring:
      profiles: peer1
    eureka:
      instance:
        hostname: peer1
      client:
        service-url:
          defaultZone: http://peer2:8762/eureka/,http://peer3:8763/eureka/
    ---
    server:
      port: 8762
    spring:
      profiles: peer2
    eureka:
      instance:
        hostname: peer2
      client:
        service-url:
          defaultZone: http://peer1:8761/eureka/,http://peer3:8763/eureka/
    ---
    server:
      port: 8763
    spring:
      profiles: peer3
    eureka:
      instance:
        hostname: peer3
      client:
        service-url:
          defaultZone: http://peer1:8761/eureka/,http://peer2:8762/eureka

defaultZone不能写成default-zone

instance-id:自定义服务在UI管理页面上显示的名称

## 修改启动文件
    
    启动类添加注解@EnableEurekaServer