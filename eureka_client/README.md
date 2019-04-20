# eureka client(eureka客户端)

## 创建普通maven项目 pom.xml添加依赖

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
 
## 修改配置文件

    server:
      port: 8764
    
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
          #defaultZone: http://peer1:8761/eureka/,http://peer2:8762/eureka/,http://peer3:8763/eureka/
    
    spring:
      application:
        name: eureka_client
        
注释掉的配置在集群模式下添加，可以只写一个(服务挂掉之后其他节点丢失，无法注册)

defaultZone不能写成default-zone