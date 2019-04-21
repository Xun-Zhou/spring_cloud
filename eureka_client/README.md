# eureka client(eureka客户端)

## 创建普通maven项目 pom.xml添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
 
## 修改配置文件

    spring:
      application:
        name: eureka-client
      profiles:
        active: client1
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
          #defaultZone: http://peer1:8761/eureka/,http://peer2:8762/eureka/,http://peer3:8763/eureka/
    ---
    server:
      port: 8764
    spring:
      profiles: client1
    ---
    server:
      port: 8765
    spring:
      profiles: client2
        
注释掉的配置在集群模式下添加，可以只写一个(服务挂掉之后其他节点丢失，无法注册)

defaultZone不能写成default-zone

这里使用多段配置 方便后面的负载模式 启动时使用--spring.profiles.active=xxx指定配置

eureka注册名称不要使用下划线 否则无法识别